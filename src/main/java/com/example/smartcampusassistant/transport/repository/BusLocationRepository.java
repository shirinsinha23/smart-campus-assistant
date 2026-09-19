package com.example.smartcampusassistant.transport.repository;

import com.example.smartcampusassistant.transport.entity.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {

    @Query("SELECT bl FROM BusLocation bl WHERE bl.bus.id = :busId ORDER BY bl.timestamp DESC")
    List<BusLocation> findLatestLocationsByBus(@Param("busId") Long busId);

    @Query("SELECT bl FROM BusLocation bl WHERE bl.bus.id = :busId AND bl.isActive = true ORDER BY bl.timestamp DESC LIMIT 1")
    Optional<BusLocation> findLatestActiveLocationByBus(@Param("busId") Long busId);

    @Query("SELECT bl FROM BusLocation bl WHERE bl.bus.status = 'ACTIVE' AND bl.isActive = true ORDER BY bl.timestamp DESC")
    List<BusLocation> findLatestLocationsForActiveBuses();
}