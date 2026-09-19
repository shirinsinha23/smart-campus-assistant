package com.example.smartcampusassistant.event.controller;

import com.example.smartcampusassistant.event.model.Certificate;
import com.example.smartcampusassistant.event.service.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@Slf4j
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Certificate> generateCertificate(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        log.info("📜 Generate certificate request for user {} and event {}", userId, eventId);
        Certificate certificate = certificateService.generateCertificate(userId, eventId);
        return new ResponseEntity<>(certificate, HttpStatus.CREATED);
    }

    @PostMapping("/generate/event/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<Certificate>> generateCertificatesForEvent(@PathVariable Long eventId) {
        log.info("📜 Generate certificates for all attendees of event {}", eventId);
        List<Certificate> certificates = certificateService.generateCertificatesForEvent(eventId);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificateById(@PathVariable Long id) {
        log.info("📋 Get certificate by id: {}", id);
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    // ✅ FIX: Get certificates by user ID (with proper endpoint)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Certificate>> getCertificatesByUser(@PathVariable Long userId) {
        log.info("📋 Get certificates for user: {}", userId);
        List<Certificate> certificates = certificateService.getCertificatesByUser(userId);
        log.info("📋 Returning {} certificates for user {}", certificates.size(), userId);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Certificate>> getCertificatesByEvent(@PathVariable Long eventId) {
        log.info("📋 Get certificates for event: {}", eventId);
        return ResponseEntity.ok(certificateService.getCertificatesByEvent(eventId));
    }

    @GetMapping
    public ResponseEntity<Certificate> getCertificateByUserAndEvent(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        log.info("📋 Get certificate for user {} and event {}", userId, eventId);
        Certificate certificate = certificateService.getCertificateByUserAndEvent(userId, eventId);
        return ResponseEntity.ok(certificate);
    }

    @GetMapping("/download/{certificateId}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long certificateId) {
        log.info("📥 Download certificate: {}", certificateId);
        byte[] pdfBytes = certificateService.downloadCertificate(certificateId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificate-" + certificateId + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/view/{certificateId}")
    public ResponseEntity<byte[]> viewCertificate(@PathVariable Long certificateId) {
        log.info("👁️ View certificate: {}", certificateId);
        byte[] pdfBytes = certificateService.downloadCertificate(certificateId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "certificate-" + certificateId + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/base64/{certificateId}")
    public ResponseEntity<String> getCertificateBase64(@PathVariable Long certificateId) {
        log.info("📋 Get certificate base64: {}", certificateId);
        String base64 = certificateService.getCertificateAsBase64(certificateId);
        return ResponseEntity.ok(base64);
    }
}