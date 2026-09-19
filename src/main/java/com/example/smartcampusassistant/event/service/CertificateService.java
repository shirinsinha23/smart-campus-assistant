package com.example.smartcampusassistant.event.service;

import com.example.smartcampusassistant.event.model.Certificate;
import com.example.smartcampusassistant.event.model.Event;
import com.example.smartcampusassistant.event.model.EventRegistration.RegistrationStatus;
import com.example.smartcampusassistant.event.repository.CertificateRepository;
import com.example.smartcampusassistant.event.repository.EventRegistrationRepository;
import com.example.smartcampusassistant.event.repository.EventRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public Certificate generateCertificate(Long userId, Long eventId) {
        log.info("📜 Generating certificate for user {} and event {}", userId, eventId);

        boolean attended = registrationRepository.existsByUserIdAndEventId(userId, eventId) &&
                registrationRepository.findByUserIdAndEventId(userId, eventId)
                        .map(reg -> reg.getStatus() == RegistrationStatus.CHECKED_IN)
                        .orElse(false);

        if (!attended) {
            throw new RuntimeException("User did not attend the event. Certificate cannot be generated.");
        }

        if (certificateRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new RuntimeException("Certificate already exists for this user and event");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Event event = eventRepository.findByIdWithClubAndUser(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        byte[] pdfBytes = generateCertificatePDF(user, event);

        Certificate certificate = Certificate.builder()
                .user(user)
                .event(event)
                .certificateUrl("/api/certificates/download/" + System.currentTimeMillis())
                .certificatePdf(pdfBytes)
                .issueDate(java.time.LocalDateTime.now())
                .isDownloaded(false)
                .build();

        certificate.generateCertificateNumber();
        Certificate saved = certificateRepository.save(certificate);
        log.info("✅ Certificate generated successfully with ID: {}", saved.getId());

        return saved;
    }

    @Transactional
    public List<Certificate> generateCertificatesForEvent(Long eventId) {
        log.info("📜 Generating certificates for all attendees of event {}", eventId);

        List<Long> attendedUserIds = registrationRepository.findRegisteredUserIdsByEventId(eventId);

        if (attendedUserIds.isEmpty()) {
            throw new RuntimeException("No attendees found for this event");
        }

        List<Certificate> certificates = new ArrayList<>();
        for (Long userId : attendedUserIds) {
            try {
                if (!certificateRepository.existsByUserIdAndEventId(userId, eventId)) {
                    Certificate cert = generateCertificate(userId, eventId);
                    certificates.add(cert);
                } else {
                    log.info("Certificate already exists for user {} and event {}", userId, eventId);
                }
            } catch (Exception e) {
                log.error("Failed to generate certificate for user {}: {}", userId, e.getMessage());
            }
        }

        log.info("✅ Generated {} certificates for event {}", certificates.size(), eventId);
        return certificates;
    }

    public Certificate getCertificateById(Long certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + certificateId));
    }

    // ✅ FIXED: Use findByUserIdWithDetails to fetch Event data eagerly
    @Transactional(readOnly = true)
    public List<Certificate> getCertificatesByUser(Long userId) {
        log.info("📋 Fetching certificates for user: {}", userId);
        // ✅ IMPORTANT: Use findByUserIdWithDetails instead of findByUserId
        List<Certificate> certificates = certificateRepository.findByUserIdWithDetails(userId);
        log.info("📋 Found {} certificates for user {}", certificates.size(), userId);

        // Log each certificate for debugging
        for (Certificate cert : certificates) {
            log.info("📋 Certificate: id={}, eventId={}, eventTitle={}, number={}",
                    cert.getId(),
                    cert.getEvent() != null ? cert.getEvent().getId() : "null",
                    cert.getEvent() != null ? cert.getEvent().getTitle() : "null",
                    cert.getCertificateNumber());
        }

        return certificates;
    }

    public List<Certificate> getCertificatesByEvent(Long eventId) {
        return certificateRepository.findByEventId(eventId);
    }

    public Certificate getCertificateByUserAndEvent(Long userId, Long eventId) {
        return certificateRepository.findByUserIdAndEventId(userId, eventId)
                .orElse(null);
    }

    @Transactional
    public byte[] downloadCertificate(Long certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + certificateId));

        certificate.setIsDownloaded(true);
        certificateRepository.save(certificate);

        return certificate.getCertificatePdf();
    }

    public String getCertificateAsBase64(Long certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + certificateId));

        if (certificate.getCertificatePdf() == null) {
            throw new RuntimeException("Certificate PDF not found");
        }

        return Base64.getEncoder().encodeToString(certificate.getCertificatePdf());
    }

    private byte[] generateCertificatePDF(User user, Event event) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD);
            Paragraph title = new Paragraph("Certificate of Participation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);

            document.add(new Paragraph("_________________________________________________",
                    new Font(Font.FontFamily.HELVETICA, 18)));

            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 18);
            Paragraph presentTo = new Paragraph("\nThis certificate is proudly presented to", bodyFont);
            presentTo.setAlignment(Element.ALIGN_CENTER);
            presentTo.setSpacingAfter(10);
            document.add(presentTo);

            Font nameFont = new Font(Font.FontFamily.HELVETICA, 32, Font.BOLD);
            Paragraph namePara = new Paragraph(user.getName().toUpperCase(), nameFont);
            namePara.setAlignment(Element.ALIGN_CENTER);
            namePara.setSpacingAfter(20);
            document.add(namePara);

            Font descFont = new Font(Font.FontFamily.HELVETICA, 16);
            Paragraph description = new Paragraph(
                    "For successfully participating in the event:",
                    descFont
            );
            description.setAlignment(Element.ALIGN_CENTER);
            description.setSpacingAfter(5);
            document.add(description);

            Font eventFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph eventPara = new Paragraph(event.getTitle(), eventFont);
            eventPara.setAlignment(Element.ALIGN_CENTER);
            eventPara.setSpacingAfter(20);
            document.add(eventPara);

            Font infoFont = new Font(Font.FontFamily.HELVETICA, 14);
            Paragraph datePara = new Paragraph(
                    "📅 Date: " + event.getEventDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                    infoFont
            );
            datePara.setAlignment(Element.ALIGN_CENTER);
            datePara.setSpacingAfter(5);
            document.add(datePara);

            Paragraph venuePara = new Paragraph(
                    "📍 Venue: " + event.getVenue(),
                    infoFont
            );
            venuePara.setAlignment(Element.ALIGN_CENTER);
            venuePara.setSpacingAfter(30);
            document.add(venuePara);

            Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            String certNumber = "CERT-" + System.currentTimeMillis();
            Paragraph certNumberPara = new Paragraph(
                    "Certificate Number: " + certNumber,
                    smallFont
            );
            certNumberPara.setAlignment(Element.ALIGN_CENTER);
            certNumberPara.setSpacingAfter(10);
            document.add(certNumberPara);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate certificate PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to generate certificate PDF", e);
        }
    }
}