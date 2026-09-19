package com.example.smartcampusassistant.lostandfound.controller;

import com.example.smartcampusassistant.lostandfound.dto.LostItemRequest;
import com.example.smartcampusassistant.lostandfound.dto.LostItemResponse;
import com.example.smartcampusassistant.lostandfound.dto.MatchResponse;
import com.example.smartcampusassistant.lostandfound.entity.ItemStatus;
import com.example.smartcampusassistant.lostandfound.service.LostAndFoundService;
import com.example.smartcampusassistant.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lost-found")
@RequiredArgsConstructor
public class LostAndFoundController {

    private final LostAndFoundService lostAndFoundService;

    @GetMapping("/all")
    public ResponseEntity<List<LostItemResponse>> getAllItems() {
        try {
            return ResponseEntity.ok(lostAndFoundService.getAllItems());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/report")
    public ResponseEntity<?> reportItem(
            @RequestPart("item") LostItemRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            LostItemResponse response = lostAndFoundService.reportItem(request, userId, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LostItemResponse>> getItemsByStatus(@PathVariable ItemStatus status) {
        return ResponseEntity.ok(lostAndFoundService.getItemsByStatus(status));
    }

    @GetMapping("/my-items")
    public ResponseEntity<List<LostItemResponse>> getMyItems() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(lostAndFoundService.getItemsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LostItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(lostAndFoundService.getItemById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LostItemResponse>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) ItemStatus status) {
        return ResponseEntity.ok(lostAndFoundService.searchItems(keyword, category, status));
    }

    @PutMapping("/{id}/claim")
    public ResponseEntity<LostItemResponse> claimItem(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(lostAndFoundService.claimItem(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        lostAndFoundService.deleteItem(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/matches")
    public ResponseEntity<List<MatchResponse>> findMatches(@PathVariable Long id) {
        return ResponseEntity.ok(lostAndFoundService.findMatches(id));
    }
}