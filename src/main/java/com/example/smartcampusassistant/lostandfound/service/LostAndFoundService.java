package com.example.smartcampusassistant.lostandfound.service;

import com.example.smartcampusassistant.exception.BadRequestException;
import com.example.smartcampusassistant.exception.ResourceNotFoundException;
import com.example.smartcampusassistant.lostandfound.dto.LostItemRequest;
import com.example.smartcampusassistant.lostandfound.dto.LostItemResponse;
import com.example.smartcampusassistant.lostandfound.dto.MatchResponse;
import com.example.smartcampusassistant.lostandfound.entity.ItemStatus;
import com.example.smartcampusassistant.lostandfound.entity.LostItem;
import com.example.smartcampusassistant.lostandfound.repository.LostItemRepository;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LostAndFoundService {

    private final LostItemRepository lostItemRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/lost-items/}")
    private String uploadDir;

    @Transactional
    public LostItemResponse reportItem(LostItemRequest request, Long userId, MultipartFile image) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Title is required");
        }
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Location is required");
        }

        // Save image
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = saveImage(image);
        }

        // Parse date
        LocalDateTime date = parseDate(request.getDate());

        // Create item
        LostItem item = new LostItem();
        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        item.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);
        item.setImageUrl(imageUrl);
        item.setLocation(request.getLocation().trim());
        item.setDate(date != null ? date : LocalDateTime.now());

        // Parse status
        if (request.getStatus() != null) {
            try {
                item.setStatus(ItemStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                item.setStatus(ItemStatus.LOST);
            }
        } else {
            item.setStatus(ItemStatus.LOST);
        }

        item.setReportedBy(user);
        item.setContactInfo(request.getContactInfo() != null ? request.getContactInfo().trim() : null);
        item.setCreatedAt(LocalDateTime.now());

        LostItem saved = lostItemRepository.save(item);

        // Check for matches if FOUND
        if (saved.getStatus() == ItemStatus.FOUND) {
            findMatchesAndNotify(saved);
        }

        return toResponse(saved);
    }

    private LocalDateTime parseDate(Object dateObj) {
        if (dateObj == null) {
            return LocalDateTime.now();
        }

        if (dateObj instanceof LocalDateTime) {
            return (LocalDateTime) dateObj;
        }

        if (dateObj instanceof String) {
            String dateStr = (String) dateObj;
            try {
                if (dateStr.contains("T")) {
                    return LocalDateTime.parse(dateStr);
                } else {
                    return LocalDateTime.parse(dateStr + "T00:00:00");
                }
            } catch (Exception e) {
                return LocalDateTime.now();
            }
        }

        return LocalDateTime.now();
    }

    private String saveImage(MultipartFile image) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = image.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(fileName);

            // Save the file
            Files.write(filePath, image.getBytes());

            // ✅ Return the correct URL path
            String imageUrl = "/uploads/lost-items/" + fileName;
            System.out.println("📸 Image saved to: " + imageUrl);
            return imageUrl;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
        }
    }

    public List<LostItemResponse> getAllItems() {
        return lostItemRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<LostItemResponse> getItemsByStatus(ItemStatus status) {
        return lostItemRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<LostItemResponse> getItemsByUser(Long userId) {
        return lostItemRepository.findByReportedById(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LostItemResponse getItemById(Long id) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        return toResponse(item);
    }

    public List<LostItemResponse> searchItems(String keyword, String category, ItemStatus status) {
        List<LostItem> items = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = lostItemRepository.searchByKeyword(keyword.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            items = lostItemRepository.findByCategory(category.trim());
        } else if (status != null) {
            items = lostItemRepository.findByStatus(status);
        } else {
            items = lostItemRepository.findAll();
        }

        // Filter by status if provided
        if (status != null && !items.isEmpty()) {
            items = items.stream()
                    .filter(item -> item.getStatus() == status)
                    .collect(Collectors.toList());
        }

        return items.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LostItemResponse claimItem(Long itemId, Long userId) {
        LostItem item = lostItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        if (item.getStatus() == ItemStatus.CLAIMED) {
            throw new BadRequestException("This item has already been claimed");
        }

        if (item.getStatus() == ItemStatus.LOST) {
            throw new BadRequestException("Cannot claim a lost item. Only found items can be claimed.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        item.setStatus(ItemStatus.CLAIMED);
        item.setClaimedBy(user);
        item = lostItemRepository.save(item);

        return toResponse(item);
    }

    @Transactional
    public void deleteItem(Long itemId, Long userId) {
        LostItem item = lostItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        if (!item.getReportedBy().getId().equals(userId)) {
            throw new BadRequestException("You don't have permission to delete this item");
        }

        lostItemRepository.delete(item);
    }

    public List<MatchResponse> findMatches(Long itemId) {
        LostItem item = lostItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        List<LostItem> potentialMatches;

        if (item.getStatus() == ItemStatus.FOUND) {
            potentialMatches = lostItemRepository.findByStatus(ItemStatus.LOST);
        } else if (item.getStatus() == ItemStatus.LOST) {
            potentialMatches = lostItemRepository.findByStatus(ItemStatus.FOUND);
        } else {
            return Collections.emptyList();
        }

        List<MatchResponse.MatchedItem> matches = new ArrayList<>();

        for (LostItem potential : potentialMatches) {
            int score = calculateMatchScore(item, potential);
            if (score >= 2) {
                matches.add(MatchResponse.MatchedItem.builder()
                        .id(potential.getId())
                        .title(potential.getTitle())
                        .category(potential.getCategory())
                        .location(potential.getLocation())
                        .reportedByName(potential.getReportedBy().getName())
                        .contactInfo(potential.getContactInfo())
                        .matchScore(score)
                        .build());
            }
        }

        matches.sort((a, b) -> b.getMatchScore() - a.getMatchScore());

        return Collections.singletonList(MatchResponse.builder()
                .itemId(item.getId())
                .itemTitle(item.getTitle())
                .matches(matches)
                .build());
    }

    private void findMatchesAndNotify(LostItem foundItem) {
        List<LostItem> lostItems = lostItemRepository.findByStatus(ItemStatus.LOST);

        for (LostItem lost : lostItems) {
            int score = calculateMatchScore(lost, foundItem);
            if (score >= 3) {
                System.out.println("🔔 Match found! Lost item: " + lost.getTitle() +
                        " matches found item: " + foundItem.getTitle() +
                        " (Score: " + score + ")");
            }
        }
    }

    private int calculateMatchScore(LostItem item1, LostItem item2) {
        int score = 0;

        if (item1.getTitle() != null && item2.getTitle() != null) {
            String title1 = item1.getTitle().toLowerCase();
            String title2 = item2.getTitle().toLowerCase();

            String[] words1 = title1.split(" ");
            String[] words2 = title2.split(" ");

            for (String w1 : words1) {
                if (w1.length() > 2) {
                    for (String w2 : words2) {
                        if (w2.length() > 2 && (w1.contains(w2) || w2.contains(w1))) {
                            score += 2;
                        }
                    }
                }
            }
        }

        if (item1.getCategory() != null && item2.getCategory() != null &&
                item1.getCategory().equalsIgnoreCase(item2.getCategory())) {
            score += 3;
        }

        if (item1.getLocation() != null && item2.getLocation() != null) {
            String loc1 = item1.getLocation().toLowerCase();
            String loc2 = item2.getLocation().toLowerCase();
            if (loc1.contains(loc2) || loc2.contains(loc1) || loc1.equals(loc2)) {
                score += 2;
            }
        }

        if (item1.getDescription() != null && item2.getDescription() != null) {
            String desc1 = item1.getDescription().toLowerCase();
            String desc2 = item2.getDescription().toLowerCase();

            String[] words1 = desc1.split(" ");
            String[] words2 = desc2.split(" ");

            for (String w1 : words1) {
                if (w1.length() > 3) {
                    for (String w2 : words2) {
                        if (w2.length() > 3 && (w1.contains(w2) || w2.contains(w1))) {
                            score += 1;
                        }
                    }
                }
            }
        }

        return score;
    }

    private LostItemResponse toResponse(LostItem item) {
        LostItemResponse response = new LostItemResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setCategory(item.getCategory());
        response.setImageUrl(item.getImageUrl());
        response.setLocation(item.getLocation());
        response.setDate(item.getDate());
        response.setStatus(item.getStatus());
        response.setContactInfo(item.getContactInfo());
        response.setCreatedAt(item.getCreatedAt());

        if (item.getReportedBy() != null) {
            response.setReportedById(item.getReportedBy().getId());
            response.setReportedByName(item.getReportedBy().getName());
        }

        if (item.getClaimedBy() != null) {
            response.setClaimedById(item.getClaimedBy().getId());
            response.setClaimedByName(item.getClaimedBy().getName());
        }

        return response;
    }
}