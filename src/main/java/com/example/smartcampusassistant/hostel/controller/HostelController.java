package com.example.smartcampusassistant.hostel.controller;

import com.example.smartcampusassistant.hostel.dto.*;
import com.example.smartcampusassistant.hostel.entity.*;
import com.example.smartcampusassistant.hostel.enums.*;
import com.example.smartcampusassistant.hostel.service.HostelService;
import com.example.smartcampusassistant.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hostel")
@RequiredArgsConstructor
@Slf4j
public class HostelController {

    private final HostelService hostelService;

    // ============================================
    // ===== HOSTEL MANAGEMENT (ADMIN) =====
    // ============================================

    @PostMapping("/hostels")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Hostel> createHostel(@RequestBody Hostel hostel) {
        log.info("📝 Create hostel: {}", hostel.getName());
        return new ResponseEntity<>(hostelService.createHostel(hostel), HttpStatus.CREATED);
    }

    @GetMapping("/hostels")
    public ResponseEntity<List<Hostel>> getAllHostels() {
        log.info("📋 Get all hostels");
        return ResponseEntity.ok(hostelService.getAllHostels());
    }

    @GetMapping("/hostels/{hostelId}")
    public ResponseEntity<Hostel> getHostelById(@PathVariable Long hostelId) {
        log.info("📋 Get hostel by ID: {}", hostelId);
        return ResponseEntity.ok(hostelService.getHostelById(hostelId));
    }

    @PutMapping("/hostels/{hostelId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Hostel> updateHostel(@PathVariable Long hostelId,
                                               @RequestBody Hostel hostelDetails) {
        log.info("📝 Update hostel: {}", hostelId);
        return ResponseEntity.ok(hostelService.updateHostel(hostelId, hostelDetails));
    }

    @DeleteMapping("/hostels/{hostelId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteHostel(@PathVariable Long hostelId) {
        log.info("🗑️ Delete hostel: {}", hostelId);
        hostelService.deleteHostel(hostelId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // ===== ROOM MANAGEMENT =====
    // ============================================

    @PostMapping("/rooms")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Room> createRoom(@RequestBody RoomRequest roomRequest,
                                           @RequestParam Long hostelId) {
        log.info("📝 Create room: {} in hostel: {}", roomRequest.getRoomNumber(), hostelId);
        return new ResponseEntity<>(hostelService.createRoom(roomRequest, hostelId), HttpStatus.CREATED);
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        log.info("📋 Get all rooms");
        return ResponseEntity.ok(hostelService.getAllRooms());
    }

    @GetMapping("/rooms/hostel/{hostelId}")
    public ResponseEntity<List<RoomDTO>> getRoomsByHostel(@PathVariable Long hostelId) {
        log.info("📋 Get rooms by hostel: {}", hostelId);
        return ResponseEntity.ok(hostelService.getRoomsByHostel(hostelId));
    }

    @GetMapping("/rooms/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(@RequestParam Long hostelId) {
        log.info("📋 Get available rooms for hostel: {}", hostelId);
        return ResponseEntity.ok(hostelService.getAvailableRooms(hostelId));
    }

    @GetMapping("/rooms/type/{roomType}")
    public ResponseEntity<List<RoomDTO>> getRoomsByType(@PathVariable RoomType roomType,
                                                        @RequestParam Long hostelId) {
        log.info("📋 Get rooms by type: {} for hostel: {}", roomType, hostelId);
        return ResponseEntity.ok(hostelService.getAvailableRoomsByType(hostelId, roomType));
    }

    @GetMapping("/rooms/facility/{facility}")
    public ResponseEntity<List<RoomDTO>> getRoomsByFacility(@PathVariable RoomFacility facility) {
        log.info("📋 Get rooms by facility: {}", facility);
        return ResponseEntity.ok(hostelService.getRoomsByFacility(facility));
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long roomId) {
        log.info("📋 Get room by ID: {}", roomId);
        return ResponseEntity.ok(hostelService.getRoomById(roomId));
    }

    @PutMapping("/rooms/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Room> updateRoom(@PathVariable Long roomId,
                                           @RequestBody RoomRequest roomRequest) {
        log.info("📝 Update room: {}", roomId);
        return ResponseEntity.ok(hostelService.updateRoom(roomId, roomRequest));
    }

    @DeleteMapping("/rooms/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        log.info("🗑️ Delete room: {}", roomId);
        hostelService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // ===== ROOM ALLOCATION =====
    // ============================================

    @PostMapping("/allocations")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<RoomAllocation> allocateRoom(@RequestParam Long studentId,
                                                       @RequestParam Long roomId) {
        Long adminId = SecurityUtils.getCurrentUserId();
        log.info("📝 Allocate room: {} to student: {} by admin: {}", roomId, studentId, adminId);
        return new ResponseEntity<>(hostelService.allocateRoom(studentId, roomId, adminId), HttpStatus.CREATED);
    }

    @DeleteMapping("/allocations/{allocationId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deallocateRoom(@PathVariable Long allocationId) {
        log.info("🗑️ Deallocate room allocation: {}", allocationId);
        hostelService.deallocateRoom(allocationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/allocations")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<RoomAllocationDTO>> getAllAllocations() {
        log.info("📋 Get all allocations");
        return ResponseEntity.ok(hostelService.getAllAllocations());
    }

    @GetMapping("/allocations/student/{studentId}")
    public ResponseEntity<RoomAllocationDTO> getStudentAllocation(@PathVariable Long studentId) {
        log.info("📋 Get allocation for student: {}", studentId);
        RoomAllocationDTO allocation = hostelService.getStudentAllocation(studentId);
        if (allocation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allocation);
    }

    @GetMapping("/allocations/room/{roomId}")
    public ResponseEntity<List<RoomAllocationDTO>> getAllocationsByRoom(@PathVariable Long roomId) {
        log.info("📋 Get allocations by room: {}", roomId);
        return ResponseEntity.ok(hostelService.getAllocationsByRoom(roomId));
    }

    // ============================================
    // ===== MAINTENANCE REQUESTS =====
    // ============================================

    @PostMapping("/maintenance")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<MaintenanceRequest> createMaintenanceRequest(@RequestBody MaintenanceRequest request) {
        Long studentId = SecurityUtils.getCurrentUserId();
        log.info("📝 Create maintenance request by student: {}", studentId);
        return new ResponseEntity<>(hostelService.createMaintenanceRequest(studentId, request), HttpStatus.CREATED);
    }

    @GetMapping("/maintenance/my")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<List<MaintenanceRequestDTO>> getMyMaintenanceRequests() {
        Long studentId = SecurityUtils.getCurrentUserId();
        log.info("📋 Get maintenance requests for student: {}", studentId);
        return ResponseEntity.ok(hostelService.getMaintenanceRequestsByStudent(studentId));
    }

    @GetMapping("/maintenance/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<MaintenanceRequestDTO>> getAllMaintenanceRequests() {
        log.info("📋 Get all maintenance requests");
        return ResponseEntity.ok(hostelService.getAllMaintenanceRequests());
    }

    @GetMapping("/maintenance/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<MaintenanceRequestDTO>> getMaintenanceRequestsByStatus(@PathVariable MaintenanceStatus status) {
        log.info("📋 Get maintenance requests by status: {}", status);
        return ResponseEntity.ok(hostelService.getMaintenanceRequestsByStatus(status));
    }

    @PutMapping("/maintenance/{requestId}/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MaintenanceRequest> updateMaintenanceStatus(@PathVariable Long requestId,
                                                                      @RequestParam MaintenanceStatus status,
                                                                      @RequestParam(required = false) String resolutionNotes) {
        log.info("📝 Update maintenance request: {} to status: {}", requestId, status);
        return ResponseEntity.ok(hostelService.updateMaintenanceStatus(requestId, status, resolutionNotes));
    }

    // ============================================
    // ===== VISITOR LOGS =====
    // ============================================

    @PostMapping("/visitors")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<VisitorLog> logVisitor(@RequestBody VisitorLog visitorLog) {
        Long studentId = SecurityUtils.getCurrentUserId();
        log.info("📝 Log visitor for student: {}", studentId);
        return new ResponseEntity<>(hostelService.logVisitor(studentId, visitorLog), HttpStatus.CREATED);
    }

    @PutMapping("/visitors/{logId}/checkout")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Void> checkoutVisitor(@PathVariable Long logId) {
        log.info("📝 Checkout visitor: {}", logId);
        hostelService.checkoutVisitor(logId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/visitors/my")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<List<VisitorLogDTO>> getMyVisitorLogs() {
        Long studentId = SecurityUtils.getCurrentUserId();
        log.info("📋 Get visitor logs for student: {}", studentId);
        return ResponseEntity.ok(hostelService.getVisitorLogsByStudent(studentId));
    }

    @GetMapping("/visitors/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<VisitorLogDTO>> getAllVisitorLogs() {
        log.info("📋 Get all visitor logs");
        return ResponseEntity.ok(hostelService.getAllVisitorLogs());
    }

    // ============================================
    // ===== HOSTEL RULES =====
    // ============================================

    @PostMapping("/rules")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<HostelRule> addRule(@RequestBody HostelRule rule,
                                              @RequestParam Long hostelId) {
        log.info("📝 Add rule to hostel: {}", hostelId);
        return new ResponseEntity<>(hostelService.addRule(hostelId, rule), HttpStatus.CREATED);
    }

    @GetMapping("/rules/hostel/{hostelId}")
    public ResponseEntity<List<HostelRule>> getRulesByHostel(@PathVariable Long hostelId) {
        log.info("📋 Get rules for hostel: {}", hostelId);
        return ResponseEntity.ok(hostelService.getRulesByHostel(hostelId));
    }

    @DeleteMapping("/rules/{ruleId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteRule(@PathVariable Long ruleId) {
        log.info("🗑️ Delete rule: {}", ruleId);
        hostelService.deleteRule(ruleId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // ===== ENUM LOOKUP ENDPOINTS =====
    // ============================================

    @GetMapping("/room-types")
    public ResponseEntity<List<Map<String, Object>>> getRoomTypes() {
        log.info("📋 Get all room types");
        List<Map<String, Object>> types = new ArrayList<>();
        for (RoomType type : RoomType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("value", type.name());
            map.put("displayName", type.getDisplayName());
            map.put("defaultCapacity", type.getDefaultCapacity());
            types.add(map);
        }
        return ResponseEntity.ok(types);
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<Map<String, String>>> getFacilities() {
        log.info("📋 Get all facilities");
        List<Map<String, String>> facilities = new ArrayList<>();
        for (RoomFacility facility : RoomFacility.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("value", facility.name());
            map.put("displayName", facility.getDisplayName());
            facilities.add(map);
        }
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/bed-types")
    public ResponseEntity<List<Map<String, String>>> getBedTypes() {
        log.info("📋 Get all bed types");
        List<Map<String, String>> bedTypes = new ArrayList<>();
        for (RoomBedType bedType : RoomBedType.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("value", bedType.name());
            map.put("displayName", bedType.getDisplayName());
            bedTypes.add(map);
        }
        return ResponseEntity.ok(bedTypes);
    }

    @GetMapping("/maintenance-categories")
    public ResponseEntity<List<Map<String, String>>> getMaintenanceCategories() {
        log.info("📋 Get maintenance categories");
        List<Map<String, String>> categories = new ArrayList<>();
        String[] cats = {"Plumbing", "Electrical", "Furniture", "AC", "WiFi", "Cleaning", "Other"};
        for (String cat : cats) {
            Map<String, String> map = new HashMap<>();
            map.put("value", cat);
            map.put("displayName", cat);
            categories.add(map);
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/maintenance-priorities")
    public ResponseEntity<List<Map<String, String>>> getMaintenancePriorities() {
        log.info("📋 Get maintenance priorities");
        List<Map<String, String>> priorities = new ArrayList<>();
        String[] prios = {"HIGH", "MEDIUM", "LOW"};
        for (String prio : prios) {
            Map<String, String> map = new HashMap<>();
            map.put("value", prio);
            map.put("displayName", prio);
            priorities.add(map);
        }
        return ResponseEntity.ok(priorities);
    }

    @GetMapping("/dashboard-stats")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        log.info("📋 Get hostel dashboard stats");
        return ResponseEntity.ok(hostelService.getDashboardStats());
    }
}