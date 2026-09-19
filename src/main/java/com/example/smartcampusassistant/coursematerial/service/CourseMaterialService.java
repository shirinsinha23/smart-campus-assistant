package com.example.smartcampusassistant.coursematerial.service;

import com.example.smartcampusassistant.coursematerial.dto.CourseMaterialRequest;
import com.example.smartcampusassistant.coursematerial.entity.CourseMaterial;
import com.example.smartcampusassistant.coursematerial.repository.CourseMaterialRepository;
import com.example.smartcampusassistant.exception.BadRequestException;
import com.example.smartcampusassistant.exception.ResourceNotFoundException;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import com.example.smartcampusassistant.user.entity.FacultySubject;
import com.example.smartcampusassistant.user.repository.FacultySubjectRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseMaterialService {

    private final CourseMaterialRepository courseMaterialRepository;
    private final UserRepository userRepository;
    private final FacultySubjectRepository facultySubjectRepository;

    @Value("${app.upload.dir:uploads/course-materials/}")
    private String uploadDir;

    @Transactional
    public CourseMaterial uploadMaterial(CourseMaterialRequest request, Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Title is required");
        }
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
            throw new BadRequestException("Subject is required");
        }
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new BadRequestException("Type is required");
        }

        // ✅ STRICT ROLE CHECK: Faculty can only upload their own subjects
        if ("FACULTY".equals(user.getRole().name())) {
            List<String> assignedSubjects = getFacultyAssignedSubjects(userId);
            if (assignedSubjects.isEmpty()) {
                throw new BadRequestException("You have no subjects assigned. Please contact admin.");
            }

            boolean isAssigned = facultySubjectRepository.existsByFacultyIdAndSubject(
                    userId, request.getSubject().trim()
            );
            if (!isAssigned) {
                throw new BadRequestException(
                        "You can only upload materials for subjects you are assigned to teach. " +
                                "Assigned subjects: " + String.join(", ", assignedSubjects)
                );
            }
        }

        if ("STUDENT".equals(user.getRole().name())) {
            throw new BadRequestException("Students are not allowed to upload materials");
        }

        String fileUrl = null;
        String fileName = null;
        Long fileSize = null;
        String videoUrl = null;
        String playlistUrl = null;

        // ✅ Handle different upload types
        if (file != null && !file.isEmpty()) {
            fileUrl = saveFile(file);
            fileName = file.getOriginalFilename();
            fileSize = file.getSize();
        } else if (request.getVideoUrl() != null && !request.getVideoUrl().trim().isEmpty()) {
            videoUrl = request.getVideoUrl().trim();
        } else if (request.getPlaylistUrl() != null && !request.getPlaylistUrl().trim().isEmpty()) {
            playlistUrl = request.getPlaylistUrl().trim();
        }

        CourseMaterial material = CourseMaterial.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .subject(request.getSubject().trim())
                .type(request.getType().trim())
                .fileUrl(fileUrl)
                .fileName(fileName)
                .fileSize(fileSize)
                .videoUrl(videoUrl)
                .playlistUrl(playlistUrl)
                .uploadedById(userId)
                .createdAt(LocalDateTime.now())
                .build();

        return courseMaterialRepository.save(material);
    }

    public List<String> getFacultyAssignedSubjects(Long facultyId) {
        return facultySubjectRepository.findByFacultyId(facultyId)
                .stream()
                .map(FacultySubject::getSubject)
                .collect(Collectors.toList());
    }

    public String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            return "/uploads/course-materials/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }

    public List<CourseMaterial> getAllMaterials(Long userId, String role) {
        if ("STUDENT".equals(role)) {
            return courseMaterialRepository.findAll();
        }

        if ("FACULTY".equals(role)) {
            List<String> assignedSubjects = getFacultyAssignedSubjects(userId);
            if (assignedSubjects.isEmpty()) {
                return List.of();
            }
            return courseMaterialRepository.findBySubjects(assignedSubjects);
        }

        return courseMaterialRepository.findAll();
    }

    public List<CourseMaterial> getMaterialsBySubject(String subject, Long userId, String role) {
        if ("FACULTY".equals(role)) {
            boolean isAssigned = facultySubjectRepository.existsByFacultyIdAndSubject(userId, subject);
            if (!isAssigned) {
                throw new BadRequestException("You are not assigned to teach " + subject);
            }
        }
        return courseMaterialRepository.findBySubject(subject);
    }

    public List<CourseMaterial> getMaterialsByType(String type) {
        return courseMaterialRepository.findByType(type);
    }

    public List<CourseMaterial> searchMaterials(String keyword, Long userId, String role) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMaterials(userId, role);
        }

        List<CourseMaterial> results = courseMaterialRepository.searchByKeyword(keyword.trim());

        if ("FACULTY".equals(role)) {
            List<String> assignedSubjects = getFacultyAssignedSubjects(userId);
            return results.stream()
                    .filter(m -> assignedSubjects.contains(m.getSubject()))
                    .collect(Collectors.toList());
        }

        return results;
    }

    public List<String> getAllSubjects() {
        return courseMaterialRepository.findDistinctSubjects();
    }

    public CourseMaterial getMaterialById(Long id) {
        return courseMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + id));
    }

    @Transactional
    public void deleteMaterial(Long id, Long userId, String role) {
        CourseMaterial material = courseMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + id));

        if ("STUDENT".equals(role)) {
            throw new BadRequestException("Students are not allowed to delete materials");
        }

        if ("FACULTY".equals(role)) {
            if (!material.getUploadedById().equals(userId)) {
                throw new BadRequestException("Faculty can only delete their own uploaded materials");
            }
        }

        if (material.getFileUrl() != null) {
            try {
                String filePath = material.getFileUrl().replace("/uploads/course-materials/", "");
                Path path = Paths.get(uploadDir + filePath);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                // Log error but continue
            }
        }

        courseMaterialRepository.delete(material);
    }

    public List<CourseMaterial> getMaterialsByUploader(Long userId) {
        return courseMaterialRepository.findByUploadedById(userId);
    }
}