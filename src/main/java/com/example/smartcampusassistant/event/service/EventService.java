package com.example.smartcampusassistant.event.service;

import com.example.smartcampusassistant.event.model.Club;
import com.example.smartcampusassistant.event.model.Event;
import com.example.smartcampusassistant.event.model.Event.EventStatus;
import com.example.smartcampusassistant.event.repository.ClubRepository;
import com.example.smartcampusassistant.event.repository.EventRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    @Value("${app.event.upload.dir:uploads/events}")
    private String uploadDir;

    @Transactional
    public Event createEvent(Event event, Long createdById, Long clubId, MultipartFile image) throws IOException {
        log.info("📝 Creating event: {}", event.getTitle());

        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + createdById));

        if (clubId != null && clubId > 0) {
            Club club = clubRepository.findById(clubId).orElse(null);
            event.setClub(club);
        } else {
            event.setClub(null);
        }

        event.setCreatedBy(createdBy);
        event.setRegisteredCount(0);
        event.setStatus(EventStatus.UPCOMING);

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            event.setImageUrl(imageUrl);
            log.info("📸 Image saved: {}", imageUrl);
        }

        Event savedEvent = eventRepository.save(event);
        log.info("✅ Event created successfully with ID: {}", savedEvent.getId());

        return savedEvent;
    }

    @Transactional
    public Event updateEvent(Long eventId, Event eventDetails, MultipartFile image) throws IOException {
        log.info("📝 Updating event: {}", eventId);

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        if (eventDetails.getTitle() != null && !eventDetails.getTitle().isEmpty()) {
            existingEvent.setTitle(eventDetails.getTitle());
        }
        if (eventDetails.getDescription() != null) {
            existingEvent.setDescription(eventDetails.getDescription());
        }
        if (eventDetails.getEventDate() != null) {
            existingEvent.setEventDate(eventDetails.getEventDate());
        }
        if (eventDetails.getEventTime() != null) {
            existingEvent.setEventTime(eventDetails.getEventTime());
        }
        if (eventDetails.getVenue() != null && !eventDetails.getVenue().isEmpty()) {
            existingEvent.setVenue(eventDetails.getVenue());
        }
        if (eventDetails.getCapacity() != null && eventDetails.getCapacity() > 0) {
            existingEvent.setCapacity(eventDetails.getCapacity());
        }
        if (eventDetails.getStatus() != null) {
            existingEvent.setStatus(eventDetails.getStatus());
        }

        if (image != null && !image.isEmpty()) {
            deleteOldImage(existingEvent.getImageUrl());
            String newImageUrl = saveImage(image);
            existingEvent.setImageUrl(newImageUrl);
            log.info("📸 New image saved: {}", newImageUrl);
        }

        Event updatedEvent = eventRepository.save(existingEvent);
        log.info("✅ Event updated successfully: {}", eventId);

        return updatedEvent;
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        log.info("🗑️ Deleting event: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        deleteOldImage(event.getImageUrl());

        eventRepository.delete(event);
        log.info("✅ Event deleted successfully: {}", eventId);
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        log.info("📋 Fetching all events");
        return eventRepository.findAllWithClubAndUser();
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long eventId) {
        log.info("📋 Fetching event by ID: {}", eventId);
        return eventRepository.findByIdWithClubAndUser(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
    }

    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        log.info("📋 Fetching upcoming events");
        return eventRepository.findUpcomingEventsWithClubAndUser(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByClub(Long clubId) {
        log.info("📋 Fetching events for club: {}", clubId);
        return eventRepository.findByClubId(clubId);
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByStatus(EventStatus status) {
        log.info("📋 Fetching events by status: {}", status);
        return eventRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Event> searchEvents(String keyword) {
        log.info("🔍 Searching events with keyword: {}", keyword);
        return eventRepository.searchEvents(keyword);
    }

    @Transactional(readOnly = true)
    public Page<Event> getEventsPaginated(Pageable pageable) {
        log.info("📋 Fetching paginated events");
        return eventRepository.findAllByOrderByEventDateAsc(pageable);
    }

    @Transactional
    public Event updateEventStatus(Long eventId, EventStatus status) {
        log.info("📝 Updating event status: {} -> {}", eventId, status);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        event.setStatus(status);
        return eventRepository.save(event);
    }

    @Transactional
    public void incrementRegisteredCount(Long eventId) {
        log.info("📈 Incrementing registered count for event: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventRepository.save(event);
        log.info("✅ Registered count incremented to: {}", event.getRegisteredCount());
    }

    @Transactional
    public void decrementRegisteredCount(Long eventId) {
        log.info("📉 Decrementing registered count for event: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        if (event.getRegisteredCount() > 0) {
            event.setRegisteredCount(event.getRegisteredCount() - 1);
            eventRepository.save(event);
            log.info("✅ Registered count decremented to: {}", event.getRegisteredCount());
        }
    }

    public boolean isEventFull(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        return event.getRegisteredCount() >= event.getCapacity();
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByCreator(Long userId) {
        log.info("📋 Fetching events created by user: {}", userId);
        return eventRepository.findByCreatedById(userId);
    }

    private String saveImage(MultipartFile image) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("📁 Created upload directory: {}", uploadDir);
        }

        String originalFilename = image.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, image.getBytes());
        log.info("💾 Image saved: {}", filePath);

        return "/uploads/events/" + fileName;
    }

    private void deleteOldImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadDir).resolve(fileName);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("🗑️ Deleted old image: {}", fileName);
                }
            } catch (Exception e) {
                log.warn("Could not delete old image: {}", e.getMessage());
            }
        }
    }
    @Transactional
    public Event registerForEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Check if already registered
        if (event.getRegisteredUsers() != null && event.getRegisteredUsers().contains(user)) {
            throw new RuntimeException("You are already registered for this event");
        }

        // Add to registered users set
        if (event.getRegisteredUsers() == null) {
            event.setRegisteredUsers(new HashSet<>());
        }
        event.getRegisteredUsers().add(user);
        event.setRegisteredCount(event.getRegisteredUsers().size());

        eventRepository.save(event);
        log.info("✅ User {} registered for event {}", userId, eventId);
        return event;
    }

    public List<Event> getRegisteredEvents(Long userId) {
        return eventRepository.findByRegisteredUsersId(userId);
    }
}