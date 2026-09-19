package com.example.smartcampusassistant.event.controller;

import com.example.smartcampusassistant.event.model.EventRegistration;
import com.example.smartcampusassistant.event.model.EventRegistration.RegistrationStatus;
import com.example.smartcampusassistant.event.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<EventRegistration> registerForEvent(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        log.info("📝 Register request: user={}, event={}", userId, eventId);
        EventRegistration registration = registrationService.registerForEvent(userId, eventId);
        return new ResponseEntity<>(registration, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelRegistration(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        log.info("❌ Cancel registration request: user={}, event={}", userId, eventId);
        registrationService.cancelRegistration(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkin/{registrationId}")
    public ResponseEntity<EventRegistration> checkInUser(@PathVariable Long registrationId) {
        log.info("✅ Check-in request: registration={}", registrationId);
        EventRegistration updated = registrationService.checkInUser(registrationId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventRegistration>> getRegistrationsByUser(@PathVariable Long userId) {
        log.info("📋 Get registrations for user: {}", userId);
        return ResponseEntity.ok(registrationService.getRegistrationsByUser(userId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventRegistration>> getRegistrationsByEvent(@PathVariable Long eventId) {
        log.info("📋 Get registrations for event: {}", eventId);
        return ResponseEntity.ok(registrationService.getRegistrationsByEvent(eventId));
    }

    @GetMapping
    public ResponseEntity<EventRegistration> getRegistrationByUserAndEvent(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        log.info("📋 Get registration: user={}, event={}", userId, eventId);
        EventRegistration registration = registrationService.getRegistrationByUserAndEvent(userId, eventId);
        return ResponseEntity.ok(registration);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<EventRegistration>> getActiveRegistrationsByUser(@PathVariable Long userId) {
        log.info("📋 Get active registrations for user: {}", userId);
        return ResponseEntity.ok(registrationService.getActiveRegistrationsByUser(userId));
    }

    @GetMapping("/event/{eventId}/checked-in")
    public ResponseEntity<List<EventRegistration>> getCheckedInRegistrations(@PathVariable Long eventId) {
        log.info("📋 Get checked-in registrations for event: {}", eventId);
        return ResponseEntity.ok(registrationService.getCheckedInRegistrations(eventId));
    }

    @GetMapping("/event/{eventId}/count")
    public ResponseEntity<Long> getRegistrationCount(@PathVariable Long eventId) {
        log.info("📋 Get registration count for event: {}", eventId);
        return ResponseEntity.ok(registrationService.getRegistrationCount(eventId));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isUserRegistered(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        log.info("📋 Check if user {} is registered for event {}", userId, eventId);
        return ResponseEntity.ok(registrationService.isUserRegistered(userId, eventId));
    }

    @GetMapping("/event/{eventId}/users")
    public ResponseEntity<List<Long>> getRegisteredUserIds(@PathVariable Long eventId) {
        log.info("📋 Get registered user IDs for event: {}", eventId);
        return ResponseEntity.ok(registrationService.getRegisteredUserIds(eventId));
    }

    @GetMapping("/qr")
    public ResponseEntity<String> getQRCodeForRegistration(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        log.info("📱 Get QR code: user={}, event={}", userId, eventId);
        String qrCode = registrationService.getQRCodeForRegistration(userId, eventId);
        return ResponseEntity.ok(qrCode);
    }

    @GetMapping("/qr/data")
    public ResponseEntity<String> getQRCodeData(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        log.info("📱 Get QR data: user={}, event={}", userId, eventId);
        String qrData = registrationService.getQRCodeData(userId, eventId);
        return ResponseEntity.ok(qrData);
    }

    // ============================================================
    // QR VERIFICATION - SIMPLE HTML (for mobile scanning)
    // ============================================================
    @GetMapping(value = "/verify", produces = "text/html")
    public ResponseEntity<String> verifyAndCheckIn(
            @RequestParam Long eventId,
            @RequestParam Long userId,
            @RequestParam(required = false) String t) {

        log.info("📱 QR Scan verification: eventId={}, userId={}, timestamp={}", eventId, userId, t);

        try {
            EventRegistration registration = registrationService.getRegistrationByUserAndEvent(userId, eventId);

            // Build response based on status
            String response;

            if (registration == null) {
                response = buildSimplePage("❌ Registration Not Found",
                        "You are not registered for this event. Please register first.",
                        "error");
                return ResponseEntity.badRequest().body(response);
            }

            if (registration.getStatus() == RegistrationStatus.CHECKED_IN) {
                response = buildSimplePage("✅ Already Checked In",
                        "You are already checked in for this event.",
                        "success");
                return ResponseEntity.ok(response);
            }

            if (registration.getStatus() == RegistrationStatus.CANCELLED) {
                response = buildSimplePage("❌ Registration Cancelled",
                        "Your registration is cancelled. Please register again.",
                        "error");
                return ResponseEntity.badRequest().body(response);
            }

            // Perform check-in
            EventRegistration updated = registrationService.checkInUser(registration.getId());
            log.info("✅ User {} checked in successfully for event {}", userId, eventId);

            String eventName = updated.getEvent().getTitle();
            String userName = updated.getUser().getName();
            String checkInTime = updated.getCheckInTime() != null ?
                    updated.getCheckInTime().toString() : "Just now";

            response = buildSuccessPage(eventName, userName, checkInTime);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during check-in: {}", e.getMessage());
            String response = buildSimplePage("❌ Check-in Failed",
                    "Error: " + e.getMessage(),
                    "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ============================================================
    // HELPER METHODS FOR HTML PAGES
    // ============================================================

    private String buildSuccessPage(String eventName, String userName, String checkInTime) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Check-in Successful</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            min-height: 100vh;\n" +
                "            margin: 0;\n" +
                "            background: #f0fdf4;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .card {\n" +
                "            background: white;\n" +
                "            padding: 40px;\n" +
                "            border-radius: 16px;\n" +
                "            box-shadow: 0 10px 40px rgba(0,0,0,0.1);\n" +
                "            max-width: 400px;\n" +
                "            width: 100%;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .icon { font-size: 64px; margin-bottom: 16px; }\n" +
                "        h1 { color: #16a34a; margin-bottom: 8px; }\n" +
                "        .event-name { \n" +
                "            background: #f0fdf4;\n" +
                "            padding: 12px;\n" +
                "            border-radius: 8px;\n" +
                "            border: 2px solid #86efac;\n" +
                "            font-size: 18px;\n" +
                "            font-weight: bold;\n" +
                "            color: #166534;\n" +
                "            margin: 16px 0;\n" +
                "        }\n" +
                "        .detail { text-align: left; margin: 16px 0; font-size: 14px; color: #4b5563; }\n" +
                "        .detail strong { color: #1e293b; }\n" +
                "        .btn {\n" +
                "            display: inline-block;\n" +
                "            padding: 12px 32px;\n" +
                "            background: #16a34a;\n" +
                "            color: white;\n" +
                "            border: none;\n" +
                "            border-radius: 8px;\n" +
                "            cursor: pointer;\n" +
                "            font-size: 16px;\n" +
                "            margin-top: 16px;\n" +
                "        }\n" +
                "        .btn:hover { background: #15803d; }\n" +
                "        .footer { margin-top: 16px; font-size: 12px; color: #94a3b8; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"card\">\n" +
                "        <div class=\"icon\">✅</div>\n" +
                "        <h1>Check-in Successful!</h1>\n" +
                "        <div class=\"event-name\">" + eventName + "</div>\n" +
                "        <div class=\"detail\">\n" +
                "            <p><strong>Student:</strong> " + userName + "</p>\n" +
                "            <p><strong>Check-in Time:</strong> " + checkInTime + "</p>\n" +
                "        </div>\n" +
                "        <button class=\"btn\" onclick=\"window.close();\">Close</button>\n" +
                "        <div class=\"footer\">Smart Campus Assistant</div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    private String buildSimplePage(String title, String message, String type) {
        String color = type.equals("success") ? "#16a34a" : "#dc2626";
        String bgColor = type.equals("success") ? "#f0fdf4" : "#fef2f2";

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>" + title + "</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            min-height: 100vh;\n" +
                "            margin: 0;\n" +
                "            background: " + bgColor + ";\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .card {\n" +
                "            background: white;\n" +
                "            padding: 40px;\n" +
                "            border-radius: 16px;\n" +
                "            box-shadow: 0 10px 40px rgba(0,0,0,0.1);\n" +
                "            max-width: 400px;\n" +
                "            width: 100%;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .icon { font-size: 64px; margin-bottom: 16px; }\n" +
                "        h1 { color: " + color + "; margin-bottom: 8px; }\n" +
                "        p { color: #4b5563; line-height: 1.6; }\n" +
                "        .btn {\n" +
                "            display: inline-block;\n" +
                "            padding: 12px 32px;\n" +
                "            background: " + color + ";\n" +
                "            color: white;\n" +
                "            border: none;\n" +
                "            border-radius: 8px;\n" +
                "            cursor: pointer;\n" +
                "            font-size: 16px;\n" +
                "            margin-top: 16px;\n" +
                "        }\n" +
                "        .btn:hover { opacity: 0.9; }\n" +
                "        .footer { margin-top: 16px; font-size: 12px; color: #94a3b8; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"card\">\n" +
                "        <div class=\"icon\">" + (type.equals("success") ? "✅" : "❌") + "</div>\n" +
                "        <h1>" + title + "</h1>\n" +
                "        <p>" + message + "</p>\n" +
                "        <button class=\"btn\" onclick=\"window.close();\">Close</button>\n" +
                "        <div class=\"footer\">Smart Campus Assistant</div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    // ============================================================
    // QR VERIFICATION - JSON (for API calls)
    // ============================================================
    @GetMapping(value = "/verify", produces = "application/json")
    public ResponseEntity<?> verifyAndCheckInJson(
            @RequestParam Long eventId,
            @RequestParam Long userId,
            @RequestParam(required = false) String t) {

        log.info("📱 QR Scan verification (JSON): eventId={}, userId={}", eventId, userId);

        try {
            EventRegistration registration = registrationService.getRegistrationByUserAndEvent(userId, eventId);
            if (registration == null) {
                return ResponseEntity.badRequest().body("Registration not found");
            }

            if (registration.getStatus() == RegistrationStatus.CHECKED_IN) {
                return ResponseEntity.badRequest().body("Already checked in");
            }

            if (registration.getStatus() == RegistrationStatus.CANCELLED) {
                return ResponseEntity.badRequest().body("Registration cancelled");
            }

            EventRegistration updated = registrationService.checkInUser(registration.getId());
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            log.error("Error during check-in: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Inner class for POST request body
    public static class QRVerificationRequest {
        private Long eventId;
        private Long userId;
        private String timestamp;

        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}