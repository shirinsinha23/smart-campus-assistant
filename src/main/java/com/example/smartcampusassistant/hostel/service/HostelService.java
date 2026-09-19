package com.example.smartcampusassistant.hostel.service;

import com.example.smartcampusassistant.exception.ResourceNotFoundException;
import com.example.smartcampusassistant.hostel.dto.*;
import com.example.smartcampusassistant.hostel.entity.*;
import com.example.smartcampusassistant.hostel.enums.*;
import com.example.smartcampusassistant.hostel.repository.*;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HostelService {

    private final HostelRepository hostelRepository;
    private final RoomRepository roomRepository;
    private final RoomAllocationRepository roomAllocationRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final VisitorLogRepository visitorLogRepository;
    private final HostelRuleRepository hostelRuleRepository;
    private final UserRepository userRepository;

    // ============================================
    // ===== HOSTEL MANAGEMENT =====
    // ============================================

    @Transactional
    public Hostel createHostel(Hostel hostel) {
        log.info("📝 Creating hostel: {}", hostel.getName());
        return hostelRepository.save(hostel);
    }

    public List<Hostel> getAllHostels() {
        return hostelRepository.findByIsActiveTrue();
    }

    public Hostel getHostelById(Long id) {
        return hostelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hostel not found with id: " + id));
    }

    @Transactional
    public Hostel updateHostel(Long id, Hostel hostelDetails) {
        Hostel hostel = getHostelById(id);
        if (hostelDetails.getName() != null) hostel.setName(hostelDetails.getName());
        if (hostelDetails.getBlock() != null) hostel.setBlock(hostelDetails.getBlock());
        if (hostelDetails.getTotalFloors() != null) hostel.setTotalFloors(hostelDetails.getTotalFloors());
        if (hostelDetails.getAddress() != null) hostel.setAddress(hostelDetails.getAddress());
        if (hostelDetails.getContactNumber() != null) hostel.setContactNumber(hostelDetails.getContactNumber());
        if (hostelDetails.getEmail() != null) hostel.setEmail(hostelDetails.getEmail());
        if (hostelDetails.getDescription() != null) hostel.setDescription(hostelDetails.getDescription());
        if (hostelDetails.getIsActive() != null) hostel.setIsActive(hostelDetails.getIsActive());
        return hostelRepository.save(hostel);
    }

    @Transactional
    public void deleteHostel(Long id) {
        Hostel hostel = getHostelById(id);
        hostel.setIsActive(false);
        hostelRepository.save(hostel);
    }

    // ============================================
    // ===== ROOM MANAGEMENT =====
    // ============================================

    @Transactional
    public Room createRoom(RoomRequest roomRequest, Long hostelId) {
        Hostel hostel = getHostelById(hostelId);

        // Calculate capacity based on room type if not provided
        Integer capacity = roomRequest.getCapacity();
        if (capacity == null || capacity == 0) {
            capacity = roomRequest.getRoomType().getDefaultCapacity();
        }

        Room room = Room.builder()
                .roomNumber(roomRequest.getRoomNumber())
                .floor(roomRequest.getFloor())
                .wing(roomRequest.getWing() != null ? roomRequest.getWing() : "A")
                .roomType(roomRequest.getRoomType())
                .bedType(roomRequest.getBedType())
                .totalRooms(roomRequest.getTotalRooms() != null ? roomRequest.getTotalRooms() : 1)
                .capacity(capacity)
                .availableSeats(capacity)
                .occupiedCount(0)
                .attachedBathrooms(roomRequest.getAttachedBathrooms() != null ? roomRequest.getAttachedBathrooms() : 1)
                .attachedWashrooms(roomRequest.getAttachedWashrooms() != null ? roomRequest.getAttachedWashrooms() : 1)
                .rentPerMonth(roomRequest.getRentPerMonth())
                .description(roomRequest.getDescription())
                .facilities(roomRequest.getFacilities() != null ? roomRequest.getFacilities() : new ArrayList<>())
                .additionalAmenities(roomRequest.getAdditionalAmenities())
                .roomSize(roomRequest.getRoomSize())
                .hasBalcony(roomRequest.getHasBalcony() != null ? roomRequest.getHasBalcony() : false)
                .hasKitchen(roomRequest.getHasKitchen() != null ? roomRequest.getHasKitchen() : false)
                .hasLivingRoom(roomRequest.getHasLivingRoom() != null ? roomRequest.getHasLivingRoom() : false)
                .furnishedType(roomRequest.getFurnishedType() != null ? roomRequest.getFurnishedType() : "Fully")
                .status(RoomStatus.AVAILABLE)
                .hostel(hostel)
                .build();

        log.info("📝 Creating room: {} ({}) in hostel: {}",
                room.getRoomNumber(), room.getRoomType().getDisplayName(), hostel.getName());
        return roomRepository.save(room);
    }

    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToEnhancedRoomDTO)
                .collect(Collectors.toList());
    }

    public List<RoomDTO> getRoomsByHostel(Long hostelId) {
        return roomRepository.findByHostelId(hostelId).stream()
                .map(this::convertToEnhancedRoomDTO)
                .collect(Collectors.toList());
    }

    public List<RoomDTO> getAvailableRooms(Long hostelId) {
        return roomRepository.findAvailableRoomsByHostel(hostelId).stream()
                .map(this::convertToEnhancedRoomDTO)
                .collect(Collectors.toList());
    }

    public List<RoomDTO> getAvailableRoomsByType(Long hostelId, RoomType roomType) {
        return roomRepository.findAvailableRoomsByHostelAndType(hostelId, roomType).stream()
                .map(this::convertToEnhancedRoomDTO)
                .collect(Collectors.toList());
    }

    public List<RoomDTO> getRoomsByFacility(RoomFacility facility) {
        return roomRepository.findByFacilitiesContaining(facility).stream()
                .map(this::convertToEnhancedRoomDTO)
                .collect(Collectors.toList());
    }

    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return convertToEnhancedRoomDTO(room);
    }

    @Transactional
    public Room updateRoom(Long id, RoomRequest roomRequest) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        if (roomRequest.getRoomNumber() != null) room.setRoomNumber(roomRequest.getRoomNumber());
        if (roomRequest.getFloor() != null) room.setFloor(roomRequest.getFloor());
        if (roomRequest.getWing() != null) room.setWing(roomRequest.getWing());
        if (roomRequest.getRoomType() != null) room.setRoomType(roomRequest.getRoomType());
        if (roomRequest.getBedType() != null) room.setBedType(roomRequest.getBedType());
        if (roomRequest.getTotalRooms() != null) room.setTotalRooms(roomRequest.getTotalRooms());
        if (roomRequest.getCapacity() != null) {
            room.setCapacity(roomRequest.getCapacity());
            // Update available seats if capacity changes
            int occupied = room.getOccupiedCount() != null ? room.getOccupiedCount() : 0;
            room.setAvailableSeats(roomRequest.getCapacity() - occupied);
        }
        if (roomRequest.getAttachedBathrooms() != null) room.setAttachedBathrooms(roomRequest.getAttachedBathrooms());
        if (roomRequest.getAttachedWashrooms() != null) room.setAttachedWashrooms(roomRequest.getAttachedWashrooms());
        if (roomRequest.getRentPerMonth() != null) room.setRentPerMonth(roomRequest.getRentPerMonth());
        if (roomRequest.getDescription() != null) room.setDescription(roomRequest.getDescription());
        if (roomRequest.getFacilities() != null) room.setFacilities(roomRequest.getFacilities());
        if (roomRequest.getAdditionalAmenities() != null) room.setAdditionalAmenities(roomRequest.getAdditionalAmenities());
        if (roomRequest.getRoomSize() != null) room.setRoomSize(roomRequest.getRoomSize());
        if (roomRequest.getHasBalcony() != null) room.setHasBalcony(roomRequest.getHasBalcony());
        if (roomRequest.getHasKitchen() != null) room.setHasKitchen(roomRequest.getHasKitchen());
        if (roomRequest.getHasLivingRoom() != null) room.setHasLivingRoom(roomRequest.getHasLivingRoom());
        if (roomRequest.getFurnishedType() != null) room.setFurnishedType(roomRequest.getFurnishedType());

        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        room.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);
    }

    // ============================================
    // ===== ROOM ALLOCATION =====
    // ============================================

    @Transactional
    public RoomAllocation allocateRoom(Long studentId, Long roomId, Long allocatedBy) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));
        User admin = userRepository.findById(allocatedBy)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + allocatedBy));

        // Check if student already has active allocation
        roomAllocationRepository.findActiveAllocationByStudent(studentId)
                .ifPresent(a -> {
                    throw new IllegalStateException("Student already has an active room allocation");
                });

        // Check if room has capacity
        long currentOccupants = roomAllocationRepository.countActiveAllocationsByRoom(roomId);
        if (currentOccupants >= room.getCapacity()) {
            throw new IllegalStateException("Room is already at full capacity");
        }

        RoomAllocation allocation = RoomAllocation.builder()
                .student(student)
                .room(room)
                .allocatedDate(LocalDate.now())
                .status(AllocationStatus.ACTIVE)
                .allocatedBy(admin)
                .build();

        // Update room status
        int newOccupiedCount = (int) currentOccupants + 1;
        room.setOccupiedCount(newOccupiedCount);
        room.setAvailableSeats(room.getCapacity() - newOccupiedCount);
        if (newOccupiedCount >= room.getCapacity()) {
            room.setStatus(RoomStatus.OCCUPIED);
        }
        roomRepository.save(room);

        log.info("✅ Room {} allocated to student {}", roomId, studentId);
        return roomAllocationRepository.save(allocation);
    }

    @Transactional
    public void deallocateRoom(Long allocationId) {
        RoomAllocation allocation = roomAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found: " + allocationId));

        allocation.setStatus(AllocationStatus.CHECKED_OUT);
        allocation.setCheckOutDate(LocalDate.now());
        roomAllocationRepository.save(allocation);

        // Update room
        Room room = allocation.getRoom();
        int newOccupiedCount = Math.max(0, room.getOccupiedCount() - 1);
        room.setOccupiedCount(newOccupiedCount);
        room.setAvailableSeats(room.getCapacity() - newOccupiedCount);
        if (newOccupiedCount < room.getCapacity()) {
            room.setStatus(RoomStatus.AVAILABLE);
        }
        roomRepository.save(room);
    }

    public RoomAllocationDTO getStudentAllocation(Long studentId) {
        return roomAllocationRepository.findActiveAllocationByStudent(studentId)
                .map(this::convertToAllocationDTO)
                .orElse(null);
    }

    public List<RoomAllocationDTO> getAllAllocations() {
        return roomAllocationRepository.findAll().stream()
                .map(this::convertToAllocationDTO)
                .collect(Collectors.toList());
    }

    public List<RoomAllocationDTO> getAllocationsByRoom(Long roomId) {
        return roomAllocationRepository.findByRoomId(roomId).stream()
                .map(this::convertToAllocationDTO)
                .collect(Collectors.toList());
    }

    // ============================================
    // ===== MAINTENANCE REQUESTS =====
    // ============================================

    @Transactional
    public MaintenanceRequest createMaintenanceRequest(Long studentId, MaintenanceRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        // Get student's room
        RoomAllocation allocation = roomAllocationRepository.findActiveAllocationByStudent(studentId)
                .orElseThrow(() -> new IllegalStateException("Student does not have an active room allocation"));

        request.setReportedBy(student);
        request.setRoom(allocation.getRoom());
        request.setStatus(MaintenanceStatus.PENDING);

        log.info("📝 Maintenance request created by student: {}", studentId);
        return maintenanceRequestRepository.save(request);
    }

    public List<MaintenanceRequestDTO> getMaintenanceRequestsByStudent(Long studentId) {
        return maintenanceRequestRepository.findByReportedById(studentId).stream()
                .map(this::convertToMaintenanceDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceRequestDTO> getAllMaintenanceRequests() {
        return maintenanceRequestRepository.findAll().stream()
                .map(this::convertToMaintenanceDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceRequestDTO> getMaintenanceRequestsByStatus(MaintenanceStatus status) {
        return maintenanceRequestRepository.findByStatus(status).stream()
                .map(this::convertToMaintenanceDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MaintenanceRequest updateMaintenanceStatus(Long requestId, MaintenanceStatus status, String resolutionNotes) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found: " + requestId));

        request.setStatus(status);
        if (status == MaintenanceStatus.RESOLVED) {
            request.setResolvedAt(LocalDateTime.now());
        }
        if (resolutionNotes != null) {
            request.setResolutionNotes(resolutionNotes);
        }

        log.info("✅ Maintenance request {} status updated to {}", requestId, status);
        return maintenanceRequestRepository.save(request);
    }

    // ============================================
    // ===== VISITOR LOGS =====
    // ============================================

    @Transactional
    public VisitorLog logVisitor(Long studentId, VisitorLog visitorLog) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        // Get student's room
        RoomAllocation allocation = roomAllocationRepository.findActiveAllocationByStudent(studentId)
                .orElseThrow(() -> new IllegalStateException("Student does not have an active room allocation"));

        visitorLog.setStudent(student);
        visitorLog.setRoom(allocation.getRoom());
        visitorLog.setCheckInTime(LocalDateTime.now());
        visitorLog.setIsActive(true);

        log.info("📝 Visitor logged for student: {}", studentId);
        return visitorLogRepository.save(visitorLog);
    }

    @Transactional
    public void checkoutVisitor(Long logId) {
        VisitorLog log = visitorLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor log not found: " + logId));
        log.setCheckOutTime(LocalDateTime.now());
        log.setIsActive(false);
        visitorLogRepository.save(log);
    }

    public List<VisitorLogDTO> getVisitorLogsByStudent(Long studentId) {
        return visitorLogRepository.findByStudentId(studentId).stream()
                .map(this::convertToVisitorLogDTO)
                .collect(Collectors.toList());
    }

    public List<VisitorLogDTO> getAllVisitorLogs() {
        return visitorLogRepository.findAll().stream()
                .map(this::convertToVisitorLogDTO)
                .collect(Collectors.toList());
    }

    // ============================================
    // ===== HOSTEL RULES =====
    // ============================================

    @Transactional
    public HostelRule addRule(Long hostelId, HostelRule rule) {
        Hostel hostel = getHostelById(hostelId);
        rule.setHostel(hostel);
        rule.setIsActive(true);
        return hostelRuleRepository.save(rule);
    }

    public List<HostelRule> getRulesByHostel(Long hostelId) {
        return hostelRuleRepository.findByHostelIdAndIsActiveTrue(hostelId);
    }

    @Transactional
    public void deleteRule(Long ruleId) {
        HostelRule rule = hostelRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found: " + ruleId));
        rule.setIsActive(false);
        hostelRuleRepository.save(rule);
    }

    // ============================================
    // ===== DASHBOARD STATS =====
    // ============================================

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Hostel stats
        stats.put("totalHostels", hostelRepository.count());
        stats.put("activeHostels", hostelRepository.findByIsActiveTrue().size());

        // Room stats
        stats.put("totalRooms", roomRepository.count());
        stats.put("availableRooms", roomRepository.findByStatus(RoomStatus.AVAILABLE).size());
        stats.put("occupiedRooms", roomRepository.findByStatus(RoomStatus.OCCUPIED).size());
        stats.put("maintenanceRooms", roomRepository.findByStatus(RoomStatus.MAINTENANCE).size());

        // Allocation stats
        stats.put("activeAllocations", roomAllocationRepository.findByStatus(AllocationStatus.ACTIVE).size());
        stats.put("totalAllocations", roomAllocationRepository.count());

        // Maintenance stats
        stats.put("pendingMaintenance", maintenanceRequestRepository.findByStatus(MaintenanceStatus.PENDING).size());
        stats.put("inProgressMaintenance", maintenanceRequestRepository.findByStatus(MaintenanceStatus.IN_PROGRESS).size());
        stats.put("resolvedMaintenance", maintenanceRequestRepository.findByStatus(MaintenanceStatus.RESOLVED).size());
        stats.put("totalMaintenance", maintenanceRequestRepository.count());

        // Visitor stats
        stats.put("activeVisitors", visitorLogRepository.countByIsActiveTrue());
        stats.put("totalVisitors", visitorLogRepository.count());

        return stats;
    }

    // ============================================
    // ===== CONVERTERS =====
    // ============================================

    private RoomDTO convertToEnhancedRoomDTO(Room room) {
        List<String> facilityDisplayNames = room.getFacilities().stream()
                .map(RoomFacility::getDisplayName)
                .collect(Collectors.toList());

        return RoomDTO.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .floor(room.getFloor())
                .wing(room.getWing())
                .roomType(room.getRoomType())
                .roomTypeDisplay(room.getRoomType().getDisplayName())
                .bedType(room.getBedType())
                .bedTypeDisplay(room.getBedType() != null ? room.getBedType().getDisplayName() : null)
                .totalRooms(room.getTotalRooms())
                .capacity(room.getCapacity())
                .availableSeats(room.getAvailableSeats())
                .occupiedCount(room.getOccupiedCount())
                .attachedBathrooms(room.getAttachedBathrooms())
                .attachedWashrooms(room.getAttachedWashrooms())
                .rentPerMonth(room.getRentPerMonth())
                .description(room.getDescription())
                .facilities(room.getFacilities())
                .facilityDisplayNames(facilityDisplayNames)
                .additionalAmenities(room.getAdditionalAmenities())
                .status(room.getStatus())
                .statusDisplay(room.getStatus().getDisplayName())
                .roomSize(room.getRoomSize())
                .hasBalcony(room.getHasBalcony())
                .hasKitchen(room.getHasKitchen())
                .hasLivingRoom(room.getHasLivingRoom())
                .furnishedType(room.getFurnishedType())
                .hostelId(room.getHostel().getId())
                .hostelName(room.getHostel().getName())
                .hostelBlock(room.getHostel().getBlock())
                .build();
    }

    private RoomAllocationDTO convertToAllocationDTO(RoomAllocation allocation) {
        return RoomAllocationDTO.builder()
                .id(allocation.getId())
                .studentId(allocation.getStudent().getId())
                .studentName(allocation.getStudent().getName())
                .studentLoginId(allocation.getStudent().getLoginId())
                .roomId(allocation.getRoom().getId())
                .roomNumber(allocation.getRoom().getRoomNumber())
                .hostelName(allocation.getRoom().getHostel().getName())
                .hostelBlock(allocation.getRoom().getHostel().getBlock())
                .allocatedDate(allocation.getAllocatedDate())
                .checkOutDate(allocation.getCheckOutDate())
                .status(allocation.getStatus())
                .statusDisplay(allocation.getStatus().getDisplayName())
                .remarks(allocation.getRemarks())
                .build();
    }

    private MaintenanceRequestDTO convertToMaintenanceDTO(MaintenanceRequest request) {
        return MaintenanceRequestDTO.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .status(request.getStatus())
                .statusDisplay(request.getStatus().getDisplayName())
                .reportedById(request.getReportedBy().getId())
                .reportedByName(request.getReportedBy().getName())
                .roomId(request.getRoom().getId())
                .roomNumber(request.getRoom().getRoomNumber())
                .resolutionNotes(request.getResolutionNotes())
                .resolvedAt(request.getResolvedAt())
                .createdAt(request.getCreatedAt())
                .build();
    }

    private VisitorLogDTO convertToVisitorLogDTO(VisitorLog log) {
        return VisitorLogDTO.builder()
                .id(log.getId())
                .visitorName(log.getVisitorName())
                .visitorPhone(log.getVisitorPhone())
                .visitorId(log.getVisitorId())
                .checkInTime(log.getCheckInTime())
                .checkOutTime(log.getCheckOutTime())
                .purpose(log.getPurpose())
                .studentId(log.getStudent().getId())
                .studentName(log.getStudent().getName())
                .roomId(log.getRoom().getId())
                .roomNumber(log.getRoom().getRoomNumber())
                .remarks(log.getRemarks())
                .isActive(log.getIsActive())
                .build();
    }
}