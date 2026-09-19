package com.example.smartcampusassistant.event.service;

import com.example.smartcampusassistant.event.model.Club;
import com.example.smartcampusassistant.event.repository.ClubRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    // Create new club
    @Transactional
    public Club createClub(Club club, Long coordinatorId) {
        if (coordinatorId != null) {
            User coordinator = userRepository.findById(coordinatorId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + coordinatorId));
            club.setCoordinator(coordinator);
        }
        club.setIsActive(true);
        return clubRepository.save(club);
    }

    // Update club
    @Transactional
    public Club updateClub(Long clubId, Club clubDetails) {
        Club existingClub = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));

        existingClub.setName(clubDetails.getName());
        existingClub.setDescription(clubDetails.getDescription());
        existingClub.setCategory(clubDetails.getCategory());
        existingClub.setLogoUrl(clubDetails.getLogoUrl());
        existingClub.setIsActive(clubDetails.getIsActive());

        if (clubDetails.getCoordinator() != null) {
            existingClub.setCoordinator(clubDetails.getCoordinator());
        }

        return clubRepository.save(existingClub);
    }

    // Delete club
    @Transactional
    public void deleteClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));
        clubRepository.delete(club);
    }

    // Get all clubs
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    // Get active clubs
    public List<Club> getActiveClubs() {
        return clubRepository.findByIsActiveTrue();
    }

    // Get club by id
    public Club getClubById(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));
    }

    // Get club by name
    public Club getClubByName(String name) {
        return clubRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Club not found with name: " + name));
    }

    // Get clubs by category
    public List<Club> getClubsByCategory(String category) {
        return clubRepository.findByCategory(category);
    }

    // Search clubs
    public List<Club> searchClubs(String keyword) {
        return clubRepository.searchClubs(keyword);
    }

    // Get clubs by coordinator
    public List<Club> getClubsByCoordinator(Long coordinatorId) {
        return clubRepository.findByCoordinatorId(coordinatorId);
    }

    // Get clubs with event count
    public List<Object[]> getClubsWithEventCount() {
        return clubRepository.findClubsWithEventCount();
    }
}