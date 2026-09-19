package com.example.smartcampusassistant.placement.service;

import com.example.smartcampusassistant.exception.ResourceNotFoundException;
import com.example.smartcampusassistant.placement.dto.*;
import com.example.smartcampusassistant.placement.entity.*;
import com.example.smartcampusassistant.placement.repository.*;
import com.example.smartcampusassistant.user.Role;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlacementService {

    private final CompanyRepository companyRepository;
    private final PlacementDriveRepository driveRepository;
    private final DriveApplicationRepository applicationRepository;
    private final InterviewScheduleRepository interviewRepository;
    private final PlacementStatisticsRepository statisticsRepository;
    private final StudentResumeRepository resumeRepository;
    private final UserRepository userRepository;

    // ============================================
    // COMPANY MANAGEMENT
    // ============================================

    @Transactional
    public Company createCompany(Company company) {
        log.info("📝 Creating company: {}", company.getName());
        return companyRepository.save(company);
    }

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findByIsActiveTrue().stream()
                .map(this::convertToCompanyDTO)
                .collect(Collectors.toList());
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
    }

    // ============================================
    // PLACEMENT DRIVE MANAGEMENT
    // ============================================

    @Transactional
    public PlacementDrive createDrive(PlacementDrive drive, Long tpoId) {
        User tpo = userRepository.findById(tpoId)
                .orElseThrow(() -> new ResourceNotFoundException("TPO not found: " + tpoId));
        drive.setCreatedBy(tpo);
        log.info("📝 Creating placement drive: {} by TPO: {}", drive.getTitle(), tpo.getName());
        return driveRepository.save(drive);
    }

    public List<PlacementDriveDTO> getAllDrives(Long studentId) {
        List<PlacementDrive> drives = driveRepository.findAll();
        return drives.stream()
                .map(drive -> convertToDriveDTO(drive, studentId))
                .collect(Collectors.toList());
    }

    public List<PlacementDriveDTO> getDrivesByStatus(String status, Long studentId) {
        List<PlacementDrive> drives = driveRepository.findByStatus(status);
        return drives.stream()
                .map(drive -> convertToDriveDTO(drive, studentId))
                .collect(Collectors.toList());
    }

    public List<PlacementDriveDTO> getUpcomingDrives(Long studentId) {
        return driveRepository.findUpcomingDrives().stream()
                .map(drive -> convertToDriveDTO(drive, studentId))
                .collect(Collectors.toList());
    }

    public PlacementDriveDTO getDriveById(Long driveId, Long studentId) {
        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found: " + driveId));
        return convertToDriveDTO(drive, studentId);
    }

    @Transactional
    public PlacementDrive updateDrive(Long driveId, PlacementDrive driveDetails) {
        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found: " + driveId));

        if (driveDetails.getTitle() != null) drive.setTitle(driveDetails.getTitle());
        if (driveDetails.getDescription() != null) drive.setDescription(driveDetails.getDescription());
        if (driveDetails.getJobRole() != null) drive.setJobRole(driveDetails.getJobRole());
        if (driveDetails.getSalaryPackage() != null) drive.setSalaryPackage(driveDetails.getSalaryPackage());
        if (driveDetails.getMinCgpa() != null) drive.setMinCgpa(driveDetails.getMinCgpa());
        if (driveDetails.getApplicationEndDate() != null) drive.setApplicationEndDate(driveDetails.getApplicationEndDate());
        if (driveDetails.getDriveDate() != null) drive.setDriveDate(driveDetails.getDriveDate());
        if (driveDetails.getStatus() != null) drive.setStatus(driveDetails.getStatus());

        return driveRepository.save(drive);
    }

    // ============================================
    // APPLICATION MANAGEMENT - FIXED (Single method)
    // ============================================

    @Transactional
    public DriveApplication applyToDrive(Long driveId, Long studentId, String resumeUrl, String coverLetter) {
        // Check if already applied
        if (applicationRepository.findByDriveIdAndStudentId(driveId, studentId).isPresent()) {
            throw new IllegalStateException("Already applied to this drive");
        }

        // Get drive and student
        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found: " + driveId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        // ✅ FIXED: Check application dates with null safety
        LocalDate today = LocalDate.now();

        if (drive.getApplicationStartDate() != null && drive.getApplicationEndDate() != null) {
            if (today.isBefore(drive.getApplicationStartDate())) {
                throw new IllegalStateException("Applications will open on: " + drive.getApplicationStartDate());
            }
            if (today.isAfter(drive.getApplicationEndDate())) {
                throw new IllegalStateException("Applications closed on: " + drive.getApplicationEndDate());
            }
        } else {
            // If no dates set, log warning but allow application
            log.warn("⚠️ Drive {} has no application dates set - allowing application", driveId);
        }

        // ✅ Check drive status allows applications
        String status = drive.getStatus();
        if (status != null && !"ONGOING".equals(status) && !"UPCOMING".equals(status)) {
            throw new IllegalStateException("Drive is not accepting applications. Current status: " + status);
        }

        // Create application
        DriveApplication application = DriveApplication.builder()
                .drive(drive)
                .student(student)
                .resumeUrl(resumeUrl)
                .coverLetter(coverLetter)
                .status("PENDING")
                .appliedAt(LocalDateTime.now())
                .build();

        log.info("📝 Student {} applied to drive: {}", studentId, drive.getTitle());
        return applicationRepository.save(application);
    }

    public List<DriveApplicationDTO> getApplicationsByStudent(Long studentId) {
        return applicationRepository.findByStudentId(studentId).stream()
                .map(this::convertToApplicationDTO)
                .collect(Collectors.toList());
    }

    public List<DriveApplicationDTO> getApplicationsByDrive(Long driveId) {
        return applicationRepository.findByDriveId(driveId).stream()
                .map(this::convertToApplicationDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DriveApplication updateApplicationStatus(Long applicationId, String status, String remarks) {
        DriveApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

        application.setStatus(status);
        if (remarks != null) application.setRemarks(remarks);

        return applicationRepository.save(application);
    }

    @Transactional
    public DriveApplication shortlistStudent(Long applicationId) {
        DriveApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));
        application.setStatus("SHORTLISTED");
        return applicationRepository.save(application);
    }

    // ============================================
    // INTERVIEW MANAGEMENT
    // ============================================

    @Transactional
    public InterviewSchedule scheduleInterview(InterviewSchedule interview) {
        DriveApplication application = applicationRepository.findById(interview.getApplication().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        interview.setApplication(application);
        interview.setStatus("SCHEDULED");

        application.setStatus("SHORTLISTED");
        applicationRepository.save(application);

        log.info("📝 Interview scheduled for application: {}", interview.getApplication().getId());
        return interviewRepository.save(interview);
    }

    public List<InterviewScheduleDTO> getInterviewsByStudent(Long studentId) {
        return interviewRepository.findByStudentId(studentId).stream()
                .map(this::convertToInterviewDTO)
                .collect(Collectors.toList());
    }

    public List<InterviewScheduleDTO> getInterviewsByApplication(Long applicationId) {
        return interviewRepository.findByApplicationId(applicationId).stream()
                .map(this::convertToInterviewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InterviewSchedule updateInterviewStatus(Long interviewId, String status, String feedback, String result) {
        InterviewSchedule interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + interviewId));

        interview.setStatus(status);
        if (feedback != null) interview.setFeedback(feedback);
        if (result != null) interview.setResult(result);

        if (result != null) {
            DriveApplication application = interview.getApplication();
            if ("SELECTED".equals(result)) {
                application.setStatus("SELECTED");
            } else if ("REJECTED".equals(result)) {
                application.setStatus("REJECTED");
            }
            applicationRepository.save(application);
        }

        return interviewRepository.save(interview);
    }

    // ============================================
    // STATISTICS
    // ============================================

    public List<PlacementStatsDTO> getPlacementStatistics() {
        return statisticsRepository.findAll().stream()
                .map(this::convertToStatsDTO)
                .collect(Collectors.toList());
    }

    public PlacementStatsDTO getLatestStatistics() {
        PlacementStatistics stats = statisticsRepository.findFirstByOrderByAcademicYearDesc()
                .orElse(null);
        return stats != null ? convertToStatsDTO(stats) : null;
    }

    public PlacementStatsDTO getStatisticsByYear(String academicYear) {
        PlacementStatistics stats = statisticsRepository.findByAcademicYear(academicYear)
                .orElse(null);
        return stats != null ? convertToStatsDTO(stats) : null;
    }

    public DashboardStatsDTO getDashboardStats() {
        long totalCompanies = companyRepository.count();
        long activeCompanies = companyRepository.findByIsActiveTrue().size();
        long totalDrives = driveRepository.count();
        long activeDrives = driveRepository.countActiveDrives();
        long totalApplications = applicationRepository.count();
        long shortlisted = applicationRepository.findByStatus("SHORTLISTED").size();
        long selected = applicationRepository.findByStatus("SELECTED").size();
        long pending = applicationRepository.findByStatus("PENDING").size();
        long rejected = applicationRepository.findByStatus("REJECTED").size();

        long totalStudents = userRepository.countByRole(Role.STUDENT);
        Double placementPercentage = totalStudents > 0 ?
                (selected * 100.0 / totalStudents) : 0.0;

        PlacementStatistics latestStats = statisticsRepository.findFirstByOrderByAcademicYearDesc().orElse(null);

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDate weekStart = LocalDate.now().minusDays(7);
        LocalDate weekEnd = LocalDate.now();
        LocalDateTime monthStart = LocalDateTime.now().minusDays(30);

        return DashboardStatsDTO.builder()
                .totalCompanies((int) totalCompanies)
                .activeCompanies((int) activeCompanies)
                .totalDrives((int) totalDrives)
                .activeDrives((int) activeDrives)
                .upcomingDrives((int) driveRepository.findByStatus("UPCOMING").size())
                .ongoingDrives((int) driveRepository.findByStatus("ONGOING").size())
                .completedDrives((int) driveRepository.findByStatus("COMPLETED").size())
                .cancelledDrives((int) driveRepository.findByStatus("CANCELLED").size())
                .totalApplications((int) totalApplications)
                .pendingApplications((int) pending)
                .shortlisted((int) shortlisted)
                .selected((int) selected)
                .rejected((int) rejected)
                .totalStudentsEligible((int) totalStudents)
                .studentsApplied((int) applicationRepository.countDistinctStudentIds())
                .studentsPlaced((int) selected)
                .studentsShortlisted((int) shortlisted)
                .placementPercentage(placementPercentage)
                .highestPackage(latestStats != null ? latestStats.getHighestPackage().doubleValue() : 0.0)
                .averagePackage(latestStats != null ? latestStats.getAveragePackage().doubleValue() : 0.0)
                .recentApplications((int) applicationRepository.countRecentApplications(sevenDaysAgo))
                .newDrives((int) driveRepository.countNewDrives(thirtyDaysAgo))
                .interviewsThisWeek((int) interviewRepository.countScheduledThisWeek(weekStart, weekEnd))
                .offersThisMonth((int) applicationRepository.countSelectedThisMonth(monthStart))
                .build();
    }

    // ============================================
    // CONVERTERS
    // ============================================

    private CompanyDTO convertToCompanyDTO(Company company) {
        long totalDrives = driveRepository.findByCompanyId(company.getId()).size();
        return CompanyDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .industry(company.getIndustry())
                .website(company.getWebsite())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .logoUrl(company.getLogoUrl())
                .description(company.getDescription())
                .isActive(company.getIsActive())
                .totalDrives((int) totalDrives)
                .build();
    }

    private PlacementDriveDTO convertToDriveDTO(PlacementDrive drive, Long studentId) {
        boolean isApplied = false;
        if (studentId != null) {
            isApplied = applicationRepository.findByDriveIdAndStudentId(drive.getId(), studentId).isPresent();
        }

        long totalApplications = applicationRepository.countByDriveId(drive.getId());

        return PlacementDriveDTO.builder()
                .id(drive.getId())
                .companyId(drive.getCompany().getId())
                .companyName(drive.getCompany().getName())
                .companyLogo(drive.getCompany().getLogoUrl())
                .title(drive.getTitle())
                .description(drive.getDescription())
                .driveType(drive.getDriveType())
                .driveTypeDisplay(getDriveTypeDisplay(drive.getDriveType()))
                .mode(drive.getMode())
                .modeDisplay(getModeDisplay(drive.getMode()))
                .jobRole(drive.getJobRole())
                .jobDescription(drive.getJobDescription())
                .salaryPackage(drive.getSalaryPackage())
                .salaryDisplay(formatSalary(drive.getSalaryPackage()))
                .minCgpa(drive.getMinCgpa())
                .maxBacklogs(drive.getMaxBacklogs())
                .eligibleBranches(drive.getEligibleBranches())
                .eligibleYears(drive.getEligibleYears())
                .applicationStartDate(drive.getApplicationStartDate())
                .applicationEndDate(drive.getApplicationEndDate())
                .driveDate(drive.getDriveDate())
                .driveTime(drive.getDriveTime())
                .venue(drive.getVenue())
                .maxApplicants(drive.getMaxApplicants())
                .totalApplications((int) totalApplications)
                .status(drive.getStatus())
                .statusDisplay(getStatusDisplay(drive.getStatus()))
                .statusColor(getStatusColor(drive.getStatus()))
                .isApplied(isApplied)
                .build();
    }

    private DriveApplicationDTO convertToApplicationDTO(DriveApplication application) {
        InterviewSchedule interview = application.getInterviews().stream()
                .filter(i -> "SCHEDULED".equals(i.getStatus()))
                .findFirst()
                .orElse(null);

        return DriveApplicationDTO.builder()
                .id(application.getId())
                .driveId(application.getDrive().getId())
                .driveTitle(application.getDrive().getTitle())
                .companyName(application.getDrive().getCompany().getName())
                .studentId(application.getStudent().getId())
                .studentName(application.getStudent().getName())
                .studentLoginId(application.getStudent().getLoginId())
                .resumeUrl(application.getResumeUrl())
                .coverLetter(application.getCoverLetter())
                .status(application.getStatus())
                .statusDisplay(getAppStatusDisplay(application.getStatus()))
                .statusColor(getAppStatusColor(application.getStatus()))
                .appliedAt(application.getAppliedAt())
                .remarks(application.getRemarks())
                .interview(interview != null ? convertToInterviewDTO(interview) : null)
                .build();
    }

    private InterviewScheduleDTO convertToInterviewDTO(InterviewSchedule interview) {
        return InterviewScheduleDTO.builder()
                .id(interview.getId())
                .applicationId(interview.getApplication().getId())
                .interviewDate(interview.getInterviewDate())
                .interviewTime(interview.getInterviewTime())
                .mode(interview.getMode())
                .modeDisplay(getModeDisplay(interview.getMode()))
                .venue(interview.getVenue())
                .meetingLink(interview.getMeetingLink())
                .interviewPanel(interview.getInterviewPanel())
                .status(interview.getStatus())
                .statusDisplay(getInterviewStatusDisplay(interview.getStatus()))
                .feedback(interview.getFeedback())
                .result(interview.getResult())
                .build();
    }

    private PlacementStatsDTO convertToStatsDTO(PlacementStatistics stats) {
        BigDecimal percentage = BigDecimal.ZERO;
        if (stats.getTotalStudents() != null && stats.getTotalStudents() > 0) {
            percentage = BigDecimal.valueOf(stats.getPlacedStudents() * 100.0 / stats.getTotalStudents())
                    .setScale(1, RoundingMode.HALF_UP);
        }

        return PlacementStatsDTO.builder()
                .academicYear(stats.getAcademicYear())
                .totalStudents(stats.getTotalStudents())
                .placedStudents(stats.getPlacedStudents())
                .placementPercentage(percentage)
                .highestPackage(stats.getHighestPackage())
                .averagePackage(stats.getAveragePackage())
                .totalCompanies(stats.getTotalCompanies())
                .totalDrives(stats.getTotalDrives())
                .activeDrives((int) driveRepository.countActiveDrives())
                .build();
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private String getDriveTypeDisplay(String type) {
        if (type == null) return "On Campus";
        return switch (type) {
            case "ON_CAMPUS" -> "On Campus";
            case "OFF_CAMPUS" -> "Off Campus";
            case "VIRTUAL" -> "Virtual";
            default -> type;
        };
    }

    private String getModeDisplay(String mode) {
        if (mode == null) return "Offline";
        return switch (mode) {
            case "OFFLINE" -> "Offline";
            case "ONLINE" -> "Online";
            case "HYBRID" -> "Hybrid";
            default -> mode;
        };
    }

    private String getStatusDisplay(String status) {
        if (status == null) return "Upcoming";
        return switch (status) {
            case "UPCOMING" -> "Upcoming";
            case "ONGOING" -> "Ongoing";
            case "COMPLETED" -> "Completed";
            case "CANCELLED" -> "Cancelled";
            default -> status;
        };
    }

    private String getStatusColor(String status) {
        if (status == null) return "blue";
        return switch (status) {
            case "UPCOMING" -> "blue";
            case "ONGOING" -> "green";
            case "COMPLETED" -> "gray";
            case "CANCELLED" -> "red";
            default -> "blue";
        };
    }

    private String getAppStatusDisplay(String status) {
        if (status == null) return "Pending";
        return switch (status) {
            case "PENDING" -> "Pending";
            case "SHORTLISTED" -> "Shortlisted";
            case "REJECTED" -> "Rejected";
            case "SELECTED" -> "Selected";
            case "WAITING" -> "Waiting";
            default -> status;
        };
    }

    private String getAppStatusColor(String status) {
        if (status == null) return "yellow";
        return switch (status) {
            case "PENDING" -> "yellow";
            case "SHORTLISTED" -> "blue";
            case "REJECTED" -> "red";
            case "SELECTED" -> "green";
            case "WAITING" -> "purple";
            default -> "yellow";
        };
    }

    private String getInterviewStatusDisplay(String status) {
        if (status == null) return "Scheduled";
        return switch (status) {
            case "SCHEDULED" -> "Scheduled";
            case "COMPLETED" -> "Completed";
            case "CANCELLED" -> "Cancelled";
            case "RESCHEDULED" -> "Rescheduled";
            default -> status;
        };
    }

    private String formatSalary(BigDecimal salary) {
        if (salary == null) return "Not Disclosed";
        if (salary.compareTo(BigDecimal.valueOf(10000000)) >= 0) {
            return "₹" + salary.divide(BigDecimal.valueOf(10000000)).setScale(1) + " Cr";
        } else if (salary.compareTo(BigDecimal.valueOf(100000)) >= 0) {
            return "₹" + salary.divide(BigDecimal.valueOf(100000)).setScale(1) + " LPA";
        }
        return "₹" + salary;
    }
}