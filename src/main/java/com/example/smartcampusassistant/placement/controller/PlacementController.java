package com.example.smartcampusassistant.placement.controller;

import com.example.smartcampusassistant.placement.dto.*;
import com.example.smartcampusassistant.placement.entity.*;
import com.example.smartcampusassistant.placement.service.PlacementService;
import com.example.smartcampusassistant.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/placement")
@RequiredArgsConstructor
@Slf4j
public class PlacementController {

    private final PlacementService placementService;

    // ============================================
    // APPLICATION ENDPOINTS WITH FILE UPLOAD
    // ============================================

    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<DriveApplication> applyToDrive(
            @RequestParam Long driveId,
            @RequestParam(required = false) MultipartFile resumeFile,
            @RequestParam(required = false) MultipartFile coverLetterFile,
            Authentication authentication) {

        Long studentId = getUserId(authentication);
        log.info("📝 Student {} applying to drive: {}", studentId, driveId);

        String resumeUrl = null;
        String coverLetterUrl = null;

        try {
            // Save resume file if provided
            if (resumeFile != null && !resumeFile.isEmpty()) {
                resumeUrl = saveFile(resumeFile, "resume", studentId);
                log.info("✅ Resume saved: {}", resumeUrl);
            }

            // Save cover letter file if provided
            if (coverLetterFile != null && !coverLetterFile.isEmpty()) {
                coverLetterUrl = saveFile(coverLetterFile, "coverletter", studentId);
                log.info("✅ Cover letter saved: {}", coverLetterUrl);
            }
        } catch (Exception e) {
            log.error("Failed to save files: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(
                placementService.applyToDrive(driveId, studentId, resumeUrl, coverLetterUrl),
                HttpStatus.CREATED
        );
    }

    // ============================================
    // HELPER METHOD TO SAVE FILES
    // ============================================

    private String saveFile(MultipartFile file, String type, Long studentId) throws IOException {
        // Create upload directory
        String uploadDir = "uploads/placement/" + studentId + "/" + type + "/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.write(filePath, file.getBytes());

        // Return URL
        return "/uploads/placement/" + studentId + "/" + type + "/" + filename;
    }

    // ============================================
    // HELPER METHOD TO GET USER ID
    // ============================================

    private Long getUserId(Authentication authentication) {
        try {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal).getId();
            }
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            log.error("Failed to get user ID from authentication", e);
            return null;
        }
    }

    // ============================================
    // OTHER ENDPOINTS (Keep existing ones)
    // ============================================

    @PostMapping("/drives")
    @PreAuthorize("hasRole('TPO') or hasRole('ADMIN')")
    public ResponseEntity<PlacementDrive> createDrive(@RequestBody PlacementDrive drive,
                                                      Authentication authentication) {
        Long tpoId = getUserId(authentication);
        log.info("📝 Creating placement drive: {} by TPO: {}", drive.getTitle(), tpoId);
        return new ResponseEntity<>(placementService.createDrive(drive, tpoId), HttpStatus.CREATED);
    }

    @GetMapping("/drives")
    public ResponseEntity<List<PlacementDriveDTO>> getAllDrives(Authentication authentication) {
        log.info("📋 Get all drives");
        Long studentId = getUserId(authentication);
        return ResponseEntity.ok(placementService.getAllDrives(studentId));
    }

    @GetMapping("/drives/{driveId}")
    public ResponseEntity<PlacementDriveDTO> getDriveById(@PathVariable Long driveId,
                                                          Authentication authentication) {
        log.info("📋 Get drive by ID: {}", driveId);
        Long studentId = getUserId(authentication);
        return ResponseEntity.ok(placementService.getDriveById(driveId, studentId));
    }

    @GetMapping("/applications/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<DriveApplicationDTO>> getMyApplications(Authentication authentication) {
        Long studentId = getUserId(authentication);
        log.info("📋 Get applications for student: {}", studentId);
        return ResponseEntity.ok(placementService.getApplicationsByStudent(studentId));
    }

    @GetMapping("/applications/drive/{driveId}")
    @PreAuthorize("hasRole('TPO') or hasRole('ADMIN')")
    public ResponseEntity<List<DriveApplicationDTO>> getApplicationsByDrive(@PathVariable Long driveId) {
        log.info("📋 Get applications for drive: {}", driveId);
        return ResponseEntity.ok(placementService.getApplicationsByDrive(driveId));
    }

    @PutMapping("/applications/{applicationId}/status")
    @PreAuthorize("hasRole('TPO') or hasRole('ADMIN')")
    public ResponseEntity<DriveApplication> updateApplicationStatus(@PathVariable Long applicationId,
                                                                    @RequestParam String status,
                                                                    @RequestParam(required = false) String remarks) {
        log.info("📝 Updating application {} status to: {}", applicationId, status);
        return ResponseEntity.ok(placementService.updateApplicationStatus(applicationId, status, remarks));
    }

    @PutMapping("/applications/{applicationId}/shortlist")
    @PreAuthorize("hasRole('TPO') or hasRole('ADMIN')")
    public ResponseEntity<DriveApplication> shortlistStudent(@PathVariable Long applicationId) {
        log.info("📝 Shortlisting application: {}", applicationId);
        return ResponseEntity.ok(placementService.shortlistStudent(applicationId));
    }

    @PostMapping("/interviews")
    @PreAuthorize("hasRole('TPO') or hasRole('ADMIN')")
    public ResponseEntity<InterviewSchedule> scheduleInterview(@RequestBody InterviewSchedule interview) {
        log.info("📝 Scheduling interview for application: {}", interview.getApplication().getId());
        return new ResponseEntity<>(placementService.scheduleInterview(interview), HttpStatus.CREATED);
    }

    @GetMapping("/interviews/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<InterviewScheduleDTO>> getMyInterviews(Authentication authentication) {
        Long studentId = getUserId(authentication);
        log.info("📋 Get interviews for student: {}", studentId);
        return ResponseEntity.ok(placementService.getInterviewsByStudent(studentId));
    }

    @PutMapping("/interviews/{interviewId}/status")
    @PreAuthorize("hasRole('TPO') or hasRole('ADMIN')")
    public ResponseEntity<InterviewSchedule> updateInterviewStatus(@PathVariable Long interviewId,
                                                                   @RequestParam String status,
                                                                   @RequestParam(required = false) String feedback,
                                                                   @RequestParam(required = false) String result) {
        log.info("📝 Updating interview {} status to: {}", interviewId, status);
        return ResponseEntity.ok(placementService.updateInterviewStatus(interviewId, status, feedback, result));
    }

    @PostMapping("/companies")
    @PreAuthorize("hasRole('TPO') or hasRole('ADMIN')")
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        log.info("📝 Creating company: {}", company.getName());
        return new ResponseEntity<>(placementService.createCompany(company), HttpStatus.CREATED);
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        log.info("📋 Get all companies");
        return ResponseEntity.ok(placementService.getAllCompanies());
    }

    @GetMapping("/statistics/latest")
    public ResponseEntity<PlacementStatsDTO> getLatestStatistics() {
        log.info("📋 Get latest placement statistics");
        PlacementStatsDTO stats = placementService.getLatestStatistics();
        return stats != null ? ResponseEntity.ok(stats) : ResponseEntity.notFound().build();
    }

    @GetMapping("/dashboard-stats")
    @PreAuthorize("hasRole('TPO') or hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        log.info("📋 Get placement dashboard stats");
        return ResponseEntity.ok(placementService.getDashboardStats());
    }
}