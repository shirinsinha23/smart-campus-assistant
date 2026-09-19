package com.example.smartcampusassistant.event.controller;

import com.example.smartcampusassistant.event.model.Club;
import com.example.smartcampusassistant.event.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    // Create club (Admin/Faculty)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Club> createClub(
            @RequestBody Club club,
            @RequestParam(required = false) Long coordinatorId) {
        Club created = clubService.createClub(club, coordinatorId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Update club
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Club> updateClub(@PathVariable Long id, @RequestBody Club club) {
        Club updated = clubService.updateClub(id, club);
        return ResponseEntity.ok(updated);
    }

    // Delete club
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }

    // Get all clubs
    @GetMapping
    public ResponseEntity<List<Club>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    // Get active clubs
    @GetMapping("/active")
    public ResponseEntity<List<Club>> getActiveClubs() {
        return ResponseEntity.ok(clubService.getActiveClubs());
    }

    // Get club by id
    @GetMapping("/{id}")
    public ResponseEntity<Club> getClubById(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getClubById(id));
    }

    // Get club by name
    @GetMapping("/name")
    public ResponseEntity<Club> getClubByName(@RequestParam String name) {
        return ResponseEntity.ok(clubService.getClubByName(name));
    }

    // Get clubs by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Club>> getClubsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(clubService.getClubsByCategory(category));
    }

    // Search clubs
    @GetMapping("/search")
    public ResponseEntity<List<Club>> searchClubs(@RequestParam String keyword) {
        return ResponseEntity.ok(clubService.searchClubs(keyword));
    }

    // Get clubs by coordinator
    @GetMapping("/coordinator/{coordinatorId}")
    public ResponseEntity<List<Club>> getClubsByCoordinator(@PathVariable Long coordinatorId) {
        return ResponseEntity.ok(clubService.getClubsByCoordinator(coordinatorId));
    }

    // Get clubs with event count
    @GetMapping("/with-event-count")
    public ResponseEntity<List<Object[]>> getClubsWithEventCount() {
        return ResponseEntity.ok(clubService.getClubsWithEventCount());
    }
}