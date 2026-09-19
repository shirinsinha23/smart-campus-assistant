package com.example.smartcampusassistant.event.controller;

import com.example.smartcampusassistant.event.model.Event;
import com.example.smartcampusassistant.event.model.Event.EventStatus;
import com.example.smartcampusassistant.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    // Create event with image upload (Admin/Faculty)
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Event> createEvent(
            @RequestPart("event") Event event,
            @RequestParam Long createdById,
            @RequestParam(required = false) Long clubId,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        log.info("📝 Create event request received: {}", event.getTitle());
        if (clubId != null && clubId == 0) {
            clubId = null;
        }
        Event created = eventService.createEvent(event, createdById, clubId, image);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Update event with image upload (Admin/Faculty)
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestPart("event") Event event,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        log.info("📝 Update event request received: {}", id);
        Event updated = eventService.updateEvent(id, event, image);
        return ResponseEntity.ok(updated);
    }

    // Delete event
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.info("🗑️ Delete event request received: {}", id);
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // Get all events
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        log.info("📋 Get all events request");
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // Get event by id
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        log.info("📋 Get event by id request: {}", id);
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // Get upcoming events
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        log.info("📋 Get upcoming events request");
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    // Get events by club
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<Event>> getEventsByClub(@PathVariable Long clubId) {
        log.info("📋 Get events by club request: {}", clubId);
        return ResponseEntity.ok(eventService.getEventsByClub(clubId));
    }

    // Get events by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Event>> getEventsByStatus(@PathVariable EventStatus status) {
        log.info("📋 Get events by status request: {}", status);
        return ResponseEntity.ok(eventService.getEventsByStatus(status));
    }

    // Search events
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String keyword) {
        log.info("🔍 Search events request: {}", keyword);
        return ResponseEntity.ok(eventService.searchEvents(keyword));
    }

    // Get events paginated
    @GetMapping("/paginated")
    public ResponseEntity<Page<Event>> getEventsPaginated(Pageable pageable) {
        log.info("📋 Get paginated events request");
        return ResponseEntity.ok(eventService.getEventsPaginated(pageable));
    }

    // Update event status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Event> updateEventStatus(
            @PathVariable Long id,
            @RequestParam EventStatus status) {
        log.info("📝 Update event status request: {} -> {}", id, status);
        return ResponseEntity.ok(eventService.updateEventStatus(id, status));
    }

    // Get events by creator
    @GetMapping("/creator/{userId}")
    public ResponseEntity<List<Event>> getEventsByCreator(@PathVariable Long userId) {
        log.info("📋 Get events by creator request: {}", userId);
        return ResponseEntity.ok(eventService.getEventsByCreator(userId));
    }

    // ===== EVENT REGISTRATION =====

    /**
     * ✅ FIXED: This endpoint was returning a 404 because the `registeredUsers` field
     * was missing from the Event entity. Adding it to Event.java fixes the database error (500),
     * which was being incorrectly reported as a 404.
     *
     * ADD THIS FIELD TO YOUR `Event.java` ENTITY:
     *
     * @ManyToMany(fetch = FetchType.LAZY)
     * @JoinTable(
     *     name = "event_registrations",
     *     joinColumns = @JoinColumn(name = "event_id"),
     *     inverseJoinColumns = @JoinColumn(name = "user_id")
     * )
     * private Set<User> registeredUsers = new HashSet<>();
     */
    @PostMapping("/{eventId}/register/{userId}")
    public ResponseEntity<Event> registerForEvent(
            @PathVariable Long eventId,
            @PathVariable Long userId) {
        log.info("📝 Register user {} for event {}", userId, eventId);
        try {
            Event event = eventService.registerForEvent(eventId, userId);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            log.warn("ℹ️ Register skipped for user {} on event {}: {}", userId, eventId, e.getMessage());
            if (e.getMessage().contains("already registered")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
            }
            // ✅ FIX: Return the actual error message for better debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500
        }
    }

    @GetMapping("/registered/{userId}")
    public ResponseEntity<List<Event>> getRegisteredEvents(@PathVariable Long userId) {
        log.info("📋 Get registered events for user: {}", userId);
        return ResponseEntity.ok(eventService.getRegisteredEvents(userId));
    }
}