package com.example.smartcampusassistant.transport.controller;

import com.example.smartcampusassistant.transport.dto.*;
import com.example.smartcampusassistant.transport.entity.BusLocation;
import com.example.smartcampusassistant.transport.service.TransportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transport")
@RequiredArgsConstructor
@Slf4j
public class TransportController {

    private final TransportService transportService;

    // ===== BUS MANAGEMENT =====

    @PostMapping("/buses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusDTO> addBus(@RequestBody BusDTO busDTO) {
        log.info("POST /api/transport/buses - Add bus: {}", busDTO.getBusNumber());
        return new ResponseEntity<>(transportService.addBus(busDTO), HttpStatus.CREATED);
    }

    @GetMapping("/buses")
    public ResponseEntity<List<BusDTO>> getAllBuses() {
        log.info("GET /api/transport/buses - Get all buses");
        return ResponseEntity.ok(transportService.getAllBuses());
    }

    @GetMapping("/buses/{busId}")
    public ResponseEntity<BusDTO> getBusById(@PathVariable Long busId) {
        log.info("GET /api/transport/buses/{} - Get bus by ID", busId);
        return ResponseEntity.ok(transportService.getBusById(busId));
    }

    @PutMapping("/buses/{busId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusDTO> updateBus(@PathVariable Long busId, @RequestBody BusDTO busDTO) {
        log.info("PUT /api/transport/buses/{} - Update bus", busId);
        return ResponseEntity.ok(transportService.updateBus(busId, busDTO));
    }

    @DeleteMapping("/buses/{busId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBus(@PathVariable Long busId) {
        log.info("DELETE /api/transport/buses/{} - Delete bus", busId);
        transportService.deleteBus(busId);
        return ResponseEntity.noContent().build();
    }

    // ===== ROUTE MANAGEMENT =====

    @PostMapping("/routes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusRouteDTO> addRoute(@RequestBody BusRouteDTO routeDTO) {
        log.info("POST /api/transport/routes - Add route: {}", routeDTO.getRouteName());
        return new ResponseEntity<>(transportService.addRoute(routeDTO), HttpStatus.CREATED);
    }

    @GetMapping("/routes")
    public ResponseEntity<List<BusRouteDTO>> getAllRoutes() {
        log.info("GET /api/transport/routes - Get all routes");
        return ResponseEntity.ok(transportService.getAllRoutes());
    }

    @GetMapping("/routes/active")
    public ResponseEntity<List<BusRouteDTO>> getActiveRoutes() {
        log.info("GET /api/transport/routes/active - Get active routes");
        return ResponseEntity.ok(transportService.getActiveRoutes());
    }

    @DeleteMapping("/routes/{routeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long routeId) {
        log.info("DELETE /api/transport/routes/{} - Delete route", routeId);
        transportService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }

    // ===== SCHEDULE MANAGEMENT =====

    @PostMapping("/schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<BusScheduleDTO> addSchedule(@RequestBody BusScheduleDTO scheduleDTO) {
        log.info("POST /api/transport/schedules - Add schedule");
        return new ResponseEntity<>(transportService.addSchedule(scheduleDTO), HttpStatus.CREATED);
    }

    @GetMapping("/schedules/day/{dayOfWeek}")
    public ResponseEntity<List<BusScheduleDTO>> getSchedulesByDay(@PathVariable String dayOfWeek) {
        log.info("GET /api/transport/schedules/day/{} - Get schedules by day", dayOfWeek);
        return ResponseEntity.ok(transportService.getSchedulesByDay(dayOfWeek));
    }

    @GetMapping("/schedules/bus/{busId}")
    public ResponseEntity<List<BusScheduleDTO>> getSchedulesByBus(@PathVariable Long busId) {
        log.info("GET /api/transport/schedules/bus/{} - Get schedules by bus", busId);
        return ResponseEntity.ok(transportService.getSchedulesByBus(busId));
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<BusScheduleDTO>> getAllSchedules() {
        log.info("GET /api/transport/schedules - Get all schedules");
        return ResponseEntity.ok(transportService.getAllSchedules());
    }

    // ===== SEAT BOOKING =====

    @PostMapping("/bookings")
    public ResponseEntity<SeatBookingDTO> bookSeat(@RequestBody BookingRequestDTO request) {
        log.info("POST /api/transport/bookings - Book seat {} for schedule: {} by user: {}",
                request.getSeatNumber(), request.getScheduleId(), request.getUserId());
        return new ResponseEntity<>(
                transportService.bookSeat(request.getScheduleId(), request.getUserId(), request.getSeatNumber()),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam Long userId) {
        log.info("DELETE /api/transport/bookings/{} - Cancel booking by user: {}", bookingId, userId);
        transportService.cancelBooking(bookingId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings/user/{userId}")
    public ResponseEntity<List<SeatBookingDTO>> getUserBookings(@PathVariable Long userId) {
        log.info("GET /api/transport/bookings/user/{} - Get user bookings", userId);
        return ResponseEntity.ok(transportService.getUserBookings(userId));
    }

    @GetMapping("/bookings/schedule/{scheduleId}")
    public ResponseEntity<List<SeatBookingDTO>> getScheduleBookings(@PathVariable Long scheduleId) {
        log.info("GET /api/transport/bookings/schedule/{} - Get schedule bookings", scheduleId);
        return ResponseEntity.ok(transportService.getScheduleBookings(scheduleId));
    }

    @GetMapping("/bookings/available-seats/{scheduleId}")
    public ResponseEntity<Map<String, Object>> getAvailableSeats(@PathVariable Long scheduleId) {
        log.info("GET /api/transport/bookings/available-seats/{} - Get available seats", scheduleId);
        return ResponseEntity.ok(transportService.getAvailableSeats(scheduleId));
    }

    // ===== BUS TRACKING =====

    @PostMapping("/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusLocation> updateBusLocation(@RequestBody BusTrackingDTO trackingDTO) {
        log.info("POST /api/transport/tracking - Update bus location for bus: {}", trackingDTO.getBusId());
        return new ResponseEntity<>(
                transportService.updateBusLocation(trackingDTO.getBusId(), trackingDTO),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/tracking/bus/{busId}")
    public ResponseEntity<BusTrackingDTO> getBusCurrentLocation(@PathVariable Long busId) {
        log.info("GET /api/transport/tracking/bus/{} - Get bus current location", busId);
        return ResponseEntity.ok(transportService.getBusCurrentLocation(busId));
    }

    @GetMapping("/tracking/all")
    public ResponseEntity<List<BusTrackingDTO>> getAllActiveBusLocations() {
        log.info("GET /api/transport/tracking/all - Get all active bus locations");
        return ResponseEntity.ok(transportService.getAllActiveBusLocations());
    }

    // ===== DASHBOARD STATS =====

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getTransportDashboardStats() {
        log.info("GET /api/transport/dashboard/stats - Get transport dashboard stats");
        return ResponseEntity.ok(transportService.getTransportDashboardStats());
    }
}