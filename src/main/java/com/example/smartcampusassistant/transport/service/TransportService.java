package com.example.smartcampusassistant.transport.service;

import com.example.smartcampusassistant.transport.dto.*;
import com.example.smartcampusassistant.transport.entity.*;
import com.example.smartcampusassistant.transport.enums.BookingStatus;
import com.example.smartcampusassistant.transport.enums.BusStatus;
import com.example.smartcampusassistant.transport.enums.SeatStatus;
import com.example.smartcampusassistant.transport.exception.BookingException;
import com.example.smartcampusassistant.transport.exception.BusNotFoundException;
import com.example.smartcampusassistant.transport.exception.SeatUnavailableException;
import com.example.smartcampusassistant.transport.repository.*;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransportService {

    private final BusRepository busRepository;
    private final BusRouteRepository busRouteRepository;
    private final BusScheduleRepository busScheduleRepository;
    private final SeatBookingRepository seatBookingRepository;
    private final BusLocationRepository busLocationRepository;
    private final UserRepository userRepository;

    // ===== BUS MANAGEMENT =====

    @Transactional
    public BusDTO addBus(BusDTO busDTO) {
        log.info("🚌 Adding new bus: {}", busDTO.getBusNumber());

        if (busRepository.findByBusNumber(busDTO.getBusNumber()).isPresent()) {
            throw new BookingException("Bus with number " + busDTO.getBusNumber() + " already exists");
        }

        // Validate required fields
        if (busDTO.getBusNumber() == null || busDTO.getBusNumber().isEmpty()) {
            throw new BookingException("Bus number is required");
        }
        if (busDTO.getDriverName() == null || busDTO.getDriverName().isEmpty()) {
            throw new BookingException("Driver name is required");
        }
        if (busDTO.getDriverPhone() == null || busDTO.getDriverPhone().isEmpty()) {
            throw new BookingException("Driver phone is required");
        }
        if (busDTO.getCapacity() == null || busDTO.getCapacity() <= 0) {
            throw new BookingException("Valid capacity is required");
        }

        Bus bus = Bus.builder()
                .busNumber(busDTO.getBusNumber())
                .driverName(busDTO.getDriverName())
                .driverPhone(busDTO.getDriverPhone())
                .capacity(busDTO.getCapacity())
                .registrationNumber(busDTO.getRegistrationNumber() != null ? busDTO.getRegistrationNumber() : busDTO.getBusNumber())
                .status(BusStatus.ACTIVE)
                .build();

        Bus saved = busRepository.save(bus);
        log.info("✅ Bus added successfully with ID: {}", saved.getId());
        return convertToBusDTO(saved);
    }

    public List<BusDTO> getAllBuses() {
        log.info("📋 Fetching all buses");
        return busRepository.findAll().stream()
                .map(this::convertToBusDTO)
                .collect(Collectors.toList());
    }

    public BusDTO getBusById(Long busId) {
        log.info("📋 Fetching bus by ID: {}", busId);
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));
        return convertToBusDTO(bus);
    }

    @Transactional
    public BusDTO updateBus(Long busId, BusDTO busDTO) {
        log.info("✏️ Updating bus: {}", busId);
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));

        if (busDTO.getDriverName() != null) {
            bus.setDriverName(busDTO.getDriverName());
        }
        if (busDTO.getDriverPhone() != null) {
            bus.setDriverPhone(busDTO.getDriverPhone());
        }
        if (busDTO.getCapacity() != null && busDTO.getCapacity() > 0) {
            bus.setCapacity(busDTO.getCapacity());
        }
        if (busDTO.getRegistrationNumber() != null) {
            bus.setRegistrationNumber(busDTO.getRegistrationNumber());
        }
        if (busDTO.getStatus() != null) {
            bus.setStatus(BusStatus.valueOf(busDTO.getStatus()));
        }

        Bus updated = busRepository.save(bus);
        log.info("✅ Bus updated successfully: {}", updated.getId());
        return convertToBusDTO(updated);
    }

    @Transactional
    public void deleteBus(Long busId) {
        log.info("🗑️ Deleting bus: {}", busId);
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));

        // Check if bus has active schedules
        List<BusSchedule> activeSchedules = busScheduleRepository.findActiveSchedulesByBus(busId);
        if (!activeSchedules.isEmpty()) {
            throw new BookingException("Cannot delete bus with active schedules. Please remove schedules first.");
        }

        busRepository.delete(bus);
        log.info("✅ Bus deleted successfully: {}", busId);
    }

    // ===== ROUTE MANAGEMENT =====

    @Transactional
    public BusRouteDTO addRoute(BusRouteDTO routeDTO) {
        log.info("🗺️ Adding new route: {}", routeDTO.getRouteName());

        if (busRouteRepository.findByRouteName(routeDTO.getRouteName()).isPresent()) {
            throw new BookingException("Route with name " + routeDTO.getRouteName() + " already exists");
        }

        BusRoute route = BusRoute.builder()
                .routeName(routeDTO.getRouteName())
                .description(routeDTO.getDescription())
                .startLocation(routeDTO.getStartLocation())
                .endLocation(routeDTO.getEndLocation())
                .routeType(routeDTO.getRouteType() != null ? routeDTO.getRouteType() : "CITY")
                .totalDistanceKm(routeDTO.getTotalDistanceKm())
                .estimatedDurationMinutes(routeDTO.getEstimatedDurationMinutes())
                .isActive(routeDTO.getIsActive() != null ? routeDTO.getIsActive() : true)
                .build();

        // Set stops as comma-separated string
        if (routeDTO.getStops() != null && !routeDTO.getStops().isEmpty()) {
            route.setStops(String.join(",", routeDTO.getStops()));
        }

        BusRoute saved = busRouteRepository.save(route);
        log.info("✅ Route added successfully with ID: {}", saved.getId());
        return convertRouteToDTO(saved);
    }

    public List<BusRouteDTO> getAllRoutes() {
        log.info("📋 Fetching all routes");
        return busRouteRepository.findAll().stream()
                .map(this::convertRouteToDTO)
                .collect(Collectors.toList());
    }

    public List<BusRouteDTO> getActiveRoutes() {
        log.info("📋 Fetching active routes");
        return busRouteRepository.findByIsActiveTrue().stream()
                .map(this::convertRouteToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRoute(Long routeId) {
        log.info("🗑️ Deleting route: {}", routeId);
        // Check if route has active schedules
        List<BusSchedule> activeSchedules = busScheduleRepository.findByRouteId(routeId);
        if (!activeSchedules.isEmpty()) {
            throw new BookingException("Cannot delete route with active schedules. Please remove schedules first.");
        }
        busRouteRepository.deleteById(routeId);
        log.info("✅ Route deleted successfully: {}", routeId);
    }

    // ===== SCHEDULE MANAGEMENT =====

    @Transactional
    public BusScheduleDTO addSchedule(BusScheduleDTO scheduleDTO) {
        log.info("📅 Adding new schedule for bus: {} on route: {}", scheduleDTO.getBusId(), scheduleDTO.getRouteId());

        Bus bus = busRepository.findById(scheduleDTO.getBusId())
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + scheduleDTO.getBusId()));

        BusRoute route = busRouteRepository.findById(scheduleDTO.getRouteId())
                .orElseThrow(() -> new BusNotFoundException("Route not found with id: " + scheduleDTO.getRouteId()));

        // Validate times
        if (scheduleDTO.getDepartureTime().isAfter(scheduleDTO.getArrivalTime())) {
            throw new BookingException("Departure time must be before arrival time");
        }

        BusSchedule schedule = BusSchedule.builder()
                .bus(bus)
                .route(route)
                .dayOfWeek(scheduleDTO.getDayOfWeek())
                .departureTime(scheduleDTO.getDepartureTime())
                .arrivalTime(scheduleDTO.getArrivalTime())
                .isActive(scheduleDTO.getIsActive() != null ? scheduleDTO.getIsActive() : true)
                .build();

        BusSchedule saved = busScheduleRepository.save(schedule);
        log.info("✅ Schedule added successfully with ID: {}", saved.getId());
        return convertToScheduleDTO(saved);
    }

    public List<BusScheduleDTO> getSchedulesByDay(String dayOfWeek) {
        log.info("📋 Fetching schedules for day: {}", dayOfWeek);
        return busScheduleRepository.findByDayOfWeekAndIsActiveTrue(dayOfWeek).stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList());
    }

    public List<BusScheduleDTO> getSchedulesByBus(Long busId) {
        log.info("📋 Fetching schedules for bus: {}", busId);
        return busScheduleRepository.findByBusId(busId).stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList());
    }

    public List<BusScheduleDTO> getAllSchedules() {
        log.info("📋 Fetching all schedules");
        return busScheduleRepository.findAll().stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList());
    }

    // ===== SEAT BOOKING =====

    @Transactional
    public SeatBookingDTO bookSeat(Long scheduleId, Long userId, String seatNumber) {
        log.info("💺 Booking seat {} for schedule: {} by user: {}", seatNumber, scheduleId, userId);

        BusSchedule schedule = busScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusNotFoundException("Schedule not found with id: " + scheduleId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusNotFoundException("User not found with id: " + userId));

        // Check if seat is available
        List<SeatBooking> existingBookings = seatBookingRepository
                .findByScheduleIdAndSeatNumber(scheduleId, seatNumber);

        if (!existingBookings.isEmpty() && existingBookings.stream()
                .anyMatch(sb -> sb.getSeatStatus() == SeatStatus.BOOKED)) {
            throw new SeatUnavailableException("Seat " + seatNumber + " is already booked");
        }

        // Check capacity
        long bookedCount = seatBookingRepository.countBookedSeatsBySchedule(scheduleId);
        if (bookedCount >= schedule.getBus().getCapacity()) {
            throw new SeatUnavailableException("No seats available on this bus");
        }

        SeatBooking booking = SeatBooking.builder()
                .user(user)
                .schedule(schedule)
                .seatNumber(seatNumber)
                .seatStatus(SeatStatus.BOOKED)
                .bookingStatus(BookingStatus.CONFIRMED)
                .travelDate(LocalDateTime.now().plusDays(1))
                .build();

        SeatBooking saved = seatBookingRepository.save(booking);
        log.info("✅ Seat booked successfully with ID: {}", saved.getId());
        return convertToBookingDTO(saved);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        log.info("❌ Cancelling booking: {} by user: {}", bookingId, userId);

        SeatBooking booking = seatBookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BookingException("You are not authorized to cancel this booking");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setSeatStatus(SeatStatus.AVAILABLE);
        booking.setCancellationReason("Cancelled by user");

        seatBookingRepository.save(booking);
        log.info("✅ Booking cancelled successfully: {}", bookingId);
    }

    public List<SeatBookingDTO> getUserBookings(Long userId) {
        log.info("📋 Fetching bookings for user: {}", userId);
        return seatBookingRepository.findByUserId(userId).stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());
    }

    public List<SeatBookingDTO> getScheduleBookings(Long scheduleId) {
        log.info("📋 Fetching bookings for schedule: {}", scheduleId);
        return seatBookingRepository.findByScheduleId(scheduleId).stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getAvailableSeats(Long scheduleId) {
        log.info("💺 Fetching available seats for schedule: {}", scheduleId);

        BusSchedule schedule = busScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusNotFoundException("Schedule not found with id: " + scheduleId));

        List<SeatBooking> bookings = seatBookingRepository.findByScheduleId(scheduleId);
        List<String> bookedSeats = bookings.stream()
                .filter(sb -> sb.getSeatStatus() == SeatStatus.BOOKED)
                .map(SeatBooking::getSeatNumber)
                .collect(Collectors.toList());

        int totalSeats = schedule.getBus().getCapacity();
        List<String> availableSeats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            String seatNum = String.valueOf(i);
            if (!bookedSeats.contains(seatNum)) {
                availableSeats.add(seatNum);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalSeats", totalSeats);
        result.put("bookedSeats", bookedSeats);
        result.put("availableSeats", availableSeats);
        result.put("availableCount", availableSeats.size());
        result.put("bookedCount", bookedSeats.size());

        return result;
    }

    // ===== BUS TRACKING =====

    @Transactional
    public BusLocation updateBusLocation(Long busId, BusTrackingDTO trackingDTO) {
        log.info("📍 Updating location for bus: {}", busId);

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));

        BusLocation location = BusLocation.builder()
                .bus(bus)
                .latitude(trackingDTO.getLatitude())
                .longitude(trackingDTO.getLongitude())
                .locationName(trackingDTO.getLocationName())
                .speedKmh(trackingDTO.getSpeedKmh())
                .headingDegrees(trackingDTO.getHeadingDegrees())
                .timestamp(LocalDateTime.now())
                .isActive(true)
                .build();

        // Update bus last known location
        bus.setLastKnownLocation(trackingDTO.getLocationName());
        bus.setLastUpdated(LocalDateTime.now());
        busRepository.save(bus);

        BusLocation saved = busLocationRepository.save(location);
        log.info("✅ Location updated for bus: {}", busId);
        return saved;
    }

    public BusTrackingDTO getBusCurrentLocation(Long busId) {
        log.info("📍 Fetching current location for bus: {}", busId);

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));

        BusLocation latestLocation = busLocationRepository
                .findLatestActiveLocationByBus(busId)
                .orElse(null);

        if (latestLocation == null) {
            throw new BusNotFoundException("No location data available for bus: " + busId);
        }

        // Get first active schedule
        List<BusSchedule> schedules = busScheduleRepository.findActiveSchedulesByBus(busId);
        long bookedCount = 0;
        if (!schedules.isEmpty()) {
            bookedCount = seatBookingRepository.countBookedSeatsBySchedule(schedules.get(0).getId());
        }

        return BusTrackingDTO.builder()
                .busId(bus.getId())
                .busNumber(bus.getBusNumber())
                .driverName(bus.getDriverName())
                .driverPhone(bus.getDriverPhone())
                .latitude(latestLocation.getLatitude())
                .longitude(latestLocation.getLongitude())
                .locationName(latestLocation.getLocationName())
                .speedKmh(latestLocation.getSpeedKmh())
                .headingDegrees(latestLocation.getHeadingDegrees())
                .timestamp(latestLocation.getTimestamp())
                .isActive(latestLocation.getIsActive())
                .availableSeats(bus.getCapacity() - (int) bookedCount)
                .totalSeats(bus.getCapacity())
                .build();
    }

    public List<BusTrackingDTO> getAllActiveBusLocations() {
        log.info("📍 Fetching all active bus locations");

        List<BusLocation> latestLocations = busLocationRepository.findLatestLocationsForActiveBuses();

        return latestLocations.stream()
                .map(location -> {
                    Bus bus = location.getBus();
                    List<BusSchedule> schedules = busScheduleRepository.findActiveSchedulesByBus(bus.getId());
                    long bookedCount = 0;
                    if (!schedules.isEmpty()) {
                        bookedCount = seatBookingRepository.countBookedSeatsBySchedule(schedules.get(0).getId());
                    }

                    return BusTrackingDTO.builder()
                            .busId(bus.getId())
                            .busNumber(bus.getBusNumber())
                            .driverName(bus.getDriverName())
                            .driverPhone(bus.getDriverPhone())
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .locationName(location.getLocationName())
                            .speedKmh(location.getSpeedKmh())
                            .headingDegrees(location.getHeadingDegrees())
                            .timestamp(location.getTimestamp())
                            .isActive(location.getIsActive())
                            .availableSeats(bus.getCapacity() - (int) bookedCount)
                            .totalSeats(bus.getCapacity())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ===== UTILITY METHODS =====

    public Map<String, Object> getTransportDashboardStats() {
        log.info("📊 Getting transport dashboard stats");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBuses", busRepository.count());
        stats.put("activeBuses", busRepository.countActiveBuses());
        stats.put("totalRoutes", busRouteRepository.count());
        stats.put("activeRoutes", (long) busRouteRepository.findByIsActiveTrue().size());
        stats.put("totalSchedules", busScheduleRepository.count());
        stats.put("totalBookings", seatBookingRepository.count());

        // Today's bookings
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<SeatBooking> todayBookings = seatBookingRepository.findUpcomingBookings(0L, todayStart);
        stats.put("todayBookings", (long) todayBookings.size());

        return stats;
    }

    // ===== CONVERSION METHODS =====

    private BusDTO convertToBusDTO(Bus bus) {
        // Get available seats from first active schedule
        List<BusSchedule> schedules = busScheduleRepository.findActiveSchedulesByBus(bus.getId());
        long bookedCount = 0;
        if (!schedules.isEmpty()) {
            bookedCount = seatBookingRepository.countBookedSeatsBySchedule(schedules.get(0).getId());
        }

        return BusDTO.builder()
                .id(bus.getId())
                .busNumber(bus.getBusNumber())
                .driverName(bus.getDriverName())
                .driverPhone(bus.getDriverPhone())
                .capacity(bus.getCapacity())
                .registrationNumber(bus.getRegistrationNumber())
                .status(bus.getStatus() != null ? bus.getStatus().name() : BusStatus.ACTIVE.name())
                .statusDisplay(bus.getStatus() != null ? bus.getStatus().getDisplayName() : BusStatus.ACTIVE.getDisplayName())
                .lastKnownLocation(bus.getLastKnownLocation())
                .lastUpdated(bus.getLastUpdated())
                .availableSeats(bus.getCapacity() - (int) bookedCount)
                .createdAt(bus.getCreatedAt())
                .build();
    }

    private BusRouteDTO convertRouteToDTO(BusRoute route) {
        return BusRouteDTO.builder()
                .id(route.getId())
                .routeName(route.getRouteName())
                .description(route.getDescription())
                .startLocation(route.getStartLocation())
                .endLocation(route.getEndLocation())
                .routeType(route.getRouteType())
                .stops(route.getStopsList())
                .totalDistanceKm(route.getTotalDistanceKm())
                .estimatedDurationMinutes(route.getEstimatedDurationMinutes())
                .isActive(route.getIsActive())
                .build();
    }

    private BusScheduleDTO convertToScheduleDTO(BusSchedule schedule) {
        long bookedCount = seatBookingRepository.countBookedSeatsBySchedule(schedule.getId());

        return BusScheduleDTO.builder()
                .id(schedule.getId())
                .busId(schedule.getBus().getId())
                .busNumber(schedule.getBus().getBusNumber())
                .driverName(schedule.getBus().getDriverName())
                .driverPhone(schedule.getBus().getDriverPhone())
                .routeId(schedule.getRoute().getId())
                .routeName(schedule.getRoute().getRouteName())
                .dayOfWeek(schedule.getDayOfWeek())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .availableSeats(schedule.getBus().getCapacity() - (int) bookedCount)
                .totalSeats(schedule.getBus().getCapacity())
                .isActive(schedule.getIsActive())
                .build();
    }

    private SeatBookingDTO convertToBookingDTO(SeatBooking booking) {
        return SeatBookingDTO.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .userLoginId(booking.getUser().getLoginId())
                .scheduleId(booking.getSchedule().getId())
                .busNumber(booking.getSchedule().getBus().getBusNumber())
                .routeName(booking.getSchedule().getRoute().getRouteName())
                .seatNumber(booking.getSeatNumber())
                .seatStatus(booking.getSeatStatus().name())
                .seatStatusDisplay(booking.getSeatStatus().getDisplayName())
                .bookingStatus(booking.getBookingStatus().name())
                .bookingStatusDisplay(booking.getBookingStatus().getDisplayName())
                .bookingDate(booking.getBookingDate())
                .travelDate(booking.getTravelDate())
                .cancellationReason(booking.getCancellationReason())
                .build();
    }
}