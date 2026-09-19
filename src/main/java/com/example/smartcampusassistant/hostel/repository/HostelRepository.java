package com.example.smartcampusassistant.hostel.repository;

import com.example.smartcampusassistant.hostel.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {

    List<Hostel> findByIsActiveTrue();

    Optional<Hostel> findByName(String name);

    List<Hostel> findByBlock(String block);

    List<Hostel> findByBlockAndIsActiveTrue(String block);

    @Query("SELECT h FROM Hostel h WHERE h.isActive = true ORDER BY h.name ASC")
    List<Hostel> findAllActiveOrderedByName();

    @Query("SELECT COUNT(h) FROM Hostel h WHERE h.isActive = true")
    long countActiveHostels();

    @Query("SELECT h.block, COUNT(h) FROM Hostel h WHERE h.isActive = true GROUP BY h.block")
    List<Object[]> countHostelsByBlock();

    @Query("SELECT h FROM Hostel h WHERE h.name LIKE %:name% AND h.isActive = true")
    List<Hostel> searchByName(@Param("name") String name);

    @Query("SELECT h FROM Hostel h WHERE h.block = :block AND h.isActive = true")
    List<Hostel> findActiveByBlock(@Param("block") String block);

    @Query("SELECT h.block, AVG(h.totalFloors) FROM Hostel h WHERE h.isActive = true GROUP BY h.block")
    List<Object[]> getAverageFloorsByBlock();
}