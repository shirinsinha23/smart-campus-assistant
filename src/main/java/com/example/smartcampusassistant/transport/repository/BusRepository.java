package com.example.smartcampusassistant.transport.repository;

import com.example.smartcampusassistant.transport.entity.Bus;
import com.example.smartcampusassistant.transport.enums.BusStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    Optional<Bus> findByBusNumber(String busNumber);

    List<Bus> findByStatus(BusStatus status);

    @Query("SELECT b FROM Bus b WHERE b.status = 'ACTIVE'")
    List<Bus> findActiveBuses();

    @Query("SELECT COUNT(b) FROM Bus b WHERE b.status = 'ACTIVE'")
    long countActiveBuses();
}