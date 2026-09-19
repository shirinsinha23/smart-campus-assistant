package com.example.smartcampusassistant.transport.repository;

import com.example.smartcampusassistant.transport.entity.BusSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface BusScheduleRepository extends JpaRepository<BusSchedule, Long> {

    List<BusSchedule> findByBusId(Long busId);

    List<BusSchedule> findByRouteId(Long routeId);

    List<BusSchedule> findByDayOfWeekAndIsActiveTrue(String dayOfWeek);

    @Query("SELECT bs FROM BusSchedule bs WHERE bs.bus.id = :busId AND bs.isActive = true")
    List<BusSchedule> findActiveSchedulesByBus(@Param("busId") Long busId);
}