package com.example.smartcampusassistant.event.repository;

import com.example.smartcampusassistant.event.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    // ✅ This method fetches Event data eagerly
    @Query("SELECT c FROM Certificate c JOIN FETCH c.user JOIN FETCH c.event WHERE c.user.id = :userId")
    List<Certificate> findByUserIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT c FROM Certificate c JOIN FETCH c.user JOIN FETCH c.event WHERE c.event.id = :eventId")
    List<Certificate> findByEventIdWithDetails(@Param("eventId") Long eventId);

    @Query("SELECT c FROM Certificate c JOIN FETCH c.user JOIN FETCH c.event WHERE c.user.id = :userId AND c.event.id = :eventId")
    Optional<Certificate> findByUserIdAndEventIdWithDetails(@Param("userId") Long userId, @Param("eventId") Long eventId);

    List<Certificate> findByUserId(Long userId);
    List<Certificate> findByEventId(Long eventId);
    Optional<Certificate> findByUserIdAndEventId(Long userId, Long eventId);
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    long countByEventId(Long eventId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    List<Certificate> findByIssueDateAfter(LocalDateTime date);
    List<Certificate> findByIsDownloadedTrue(Long userId);
}