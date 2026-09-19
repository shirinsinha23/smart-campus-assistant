package com.example.smartcampusassistant.assignment;

import com.example.smartcampusassistant.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Value("${app.assignment.upload.dir:uploads/assignments}")
    private String uploadDir;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<Assignment> create(
            @RequestPart("assignment") Assignment incoming,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        Long userId = SecurityUtils.getCurrentUserId();
        log.info("📝 Create assignment: {} (faculty {})", incoming.getTitle(), userId);

        if (file != null && !file.isEmpty()) {
            String savedUrl = saveFile(file);
            incoming.setQuestionFileUrl(savedUrl);
            incoming.setQuestionFileName(file.getOriginalFilename());
            log.info("📎 Question paper saved: {}", savedUrl);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.create(userId, incoming));
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> listAll() {
        return ResponseEntity.ok(assignmentService.listAll());
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<List<Assignment>> listMine() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(assignmentService.listByFaculty(userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + ext;
        Path target = dir.resolve(filename);
        Files.write(target, file.getBytes());
        return "/uploads/assignments/" + filename;
    }
}