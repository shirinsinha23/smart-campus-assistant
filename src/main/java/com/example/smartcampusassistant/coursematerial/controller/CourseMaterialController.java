package com.example.smartcampusassistant.coursematerial.controller;

import com.example.smartcampusassistant.coursematerial.dto.CourseMaterialRequest;
import com.example.smartcampusassistant.coursematerial.entity.CourseMaterial;
import com.example.smartcampusassistant.coursematerial.service.CourseMaterialService;
import com.example.smartcampusassistant.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-material")
@RequiredArgsConstructor
public class CourseMaterialController {

    private final CourseMaterialService courseMaterialService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMaterial(
            @RequestPart("material") CourseMaterialRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            CourseMaterial material = courseMaterialService.uploadMaterial(request, userId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(material);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseMaterial>> getAllMaterials() {
        Long userId = SecurityUtils.getCurrentUserId();
        String role = SecurityUtils.getCurrentUserRole();
        return ResponseEntity.ok(courseMaterialService.getAllMaterials(userId, role));
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<?> getMaterialsBySubject(@PathVariable String subject) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String role = SecurityUtils.getCurrentUserRole();
            return ResponseEntity.ok(courseMaterialService.getMaterialsBySubject(subject, userId, role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CourseMaterial>> getMaterialsByType(@PathVariable String type) {
        return ResponseEntity.ok(courseMaterialService.getMaterialsByType(type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseMaterial>> searchMaterials(@RequestParam(required = false) String keyword) {
        Long userId = SecurityUtils.getCurrentUserId();
        String role = SecurityUtils.getCurrentUserRole();
        return ResponseEntity.ok(courseMaterialService.searchMaterials(keyword, userId, role));
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<String>> getAllSubjects() {
        return ResponseEntity.ok(courseMaterialService.getAllSubjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseMaterial> getMaterialById(@PathVariable Long id) {
        return ResponseEntity.ok(courseMaterialService.getMaterialById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMaterial(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String role = SecurityUtils.getCurrentUserRole();
            courseMaterialService.deleteMaterial(id, userId, role);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-subjects")
    public ResponseEntity<List<String>> getMySubjects() {
        Long userId = SecurityUtils.getCurrentUserId();
        String role = SecurityUtils.getCurrentUserRole();

        if (!"FACULTY".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(courseMaterialService.getFacultyAssignedSubjects(userId));
    }
}