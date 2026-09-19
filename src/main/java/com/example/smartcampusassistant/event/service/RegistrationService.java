package com.example.smartcampusassistant.event.service;

import com.example.smartcampusassistant.event.model.Event;
import com.example.smartcampusassistant.event.model.EventRegistration;
import com.example.smartcampusassistant.event.model.EventRegistration.RegistrationStatus;
import com.example.smartcampusassistant.event.repository.EventRegistrationRepository;
import com.example.smartcampusassistant.event.repository.EventRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final QRCodeService qrCodeService;

    @Value("${server.base.url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public EventRegistration registerForEvent(Long userId, Long eventId) {
        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new RuntimeException("User is already registered for this event");
        }

        if (eventService.isEventFull(eventId)) {
            throw new RuntimeException("Event is full. Cannot register.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Event event = eventRepository.findByIdWithClubAndUser(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        EventRegistration registration = new EventRegistration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus(RegistrationStatus.REGISTERED);

        // ✅ Generate QR code as a URL that can be scanned by mobile camera
        String qrData = baseUrl + "/api/registrations/verify?eventId=" + eventId + "&userId=" + userId + "&t=" + System.currentTimeMillis();
        String qrCodeImage = qrCodeService.generateQRCodeAsBase64(qrData);

        registration.setQrCode(qrData);
        registration.setQrCodeImage(qrCodeImage);

        eventService.incrementRegisteredCount(eventId);

        log.info("✅ User {} registered for event {} with QR code generated", userId, eventId);
        log.info("📱 QR Code URL: {}", qrData);
        return registrationRepository.save(registration);
    }

    @Transactional
    public void cancelRegistration(Long userId, Long eventId) {
        EventRegistration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (registration.getStatus() == RegistrationStatus.CHECKED_IN) {
            throw new RuntimeException("Cannot cancel registration after check-in");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
        eventService.decrementRegisteredCount(eventId);

        log.info("❌ User {} cancelled registration for event {}", userId, eventId);
    }

    @Transactional
    public EventRegistration checkInUser(Long registrationId) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found with id: " + registrationId));

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new RuntimeException("Registration is cancelled. Cannot check-in.");
        }

        if (registration.getStatus() == RegistrationStatus.CHECKED_IN) {
            throw new RuntimeException("User already checked-in");
        }

        registration.setStatus(RegistrationStatus.CHECKED_IN);
        registration.setCheckInTime(LocalDateTime.now());

        log.info("✅ User {} checked-in for event {}", registration.getUser().getId(), registration.getEvent().getId());
        return registrationRepository.save(registration);
    }

    public List<EventRegistration> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

    public List<EventRegistration> getRegistrationsByEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public EventRegistration getRegistrationByUserAndEvent(Long userId, Long eventId) {
        return registrationRepository.findByUserIdAndEventId(userId, eventId).orElse(null);
    }

    public List<EventRegistration> getActiveRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserIdAndStatus(userId, RegistrationStatus.REGISTERED);
    }

    public List<EventRegistration> getCheckedInRegistrations(Long eventId) {
        return registrationRepository.findByEventIdAndStatusAndCheckInTimeIsNotNull(eventId, RegistrationStatus.CHECKED_IN);
    }

    public long getRegistrationCount(Long eventId) {
        return registrationRepository.countByEventId(eventId);
    }

    public boolean isUserRegistered(Long userId, Long eventId) {
        return registrationRepository.existsByUserIdAndEventId(userId, eventId);
    }

    public List<Long> getRegisteredUserIds(Long eventId) {
        return registrationRepository.findRegisteredUserIdsByEventId(eventId);
    }

    public String getQRCodeForRegistration(Long userId, Long eventId) {
        EventRegistration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        return registration.getQrCodeImage();
    }

    public String getQRCodeData(Long userId, Long eventId) {
        EventRegistration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        return registration.getQrCode();
    }
}