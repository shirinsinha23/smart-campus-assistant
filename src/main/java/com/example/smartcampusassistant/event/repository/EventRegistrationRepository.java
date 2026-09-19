package com.example.smartcampusassistant.event.repository;

import com.example.smartcampusassistant.event.model.EventRegistration;
import com.example.smartcampusassistant.event.model.EventRegistration.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    List<EventRegistration> findByUserId(Long userId);

    List<EventRegistration> findByEventId(Long eventId);

    Optional<EventRegistration> findByUserIdAndEventId(Long userId, Long eventId);

    List<EventRegistration> findByStatus(RegistrationStatus status);

    List<EventRegistration> findByEventIdAndStatus(Long eventId, RegistrationStatus status);

    List<EventRegistration> findByEventIdAndStatusAndCheckInTimeIsNotNull(Long eventId, RegistrationStatus status);

    List<EventRegistration> findByUserIdAndStatus(Long userId, RegistrationStatus status);

    long countByEventId(Long eventId);

    long countByEventIdAndStatus(Long eventId, RegistrationStatus status);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT er.user.id FROM EventRegistration er WHERE er.event.id = :eventId AND er.status = 'CHECKED_IN'")
    List<Long> findRegisteredUserIdsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT er.qrCode FROM EventRegistration er WHERE er.user.id = :userId AND er.event.id = :eventId")
    Optional<String> findQrCodeByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Modifying
    @Transactional
    @Query("UPDATE EventRegistration er SET er.checkInTime = :checkInTime, er.status = 'CHECKED_IN' WHERE er.id = :registrationId")
    int updateCheckIn(@Param("registrationId") Long registrationId, @Param("checkInTime") LocalDateTime checkInTime);

    @Query("SELECT er FROM EventRegistration er WHERE er.event.id = :eventId AND er.qrCode IS NULL")
    List<EventRegistration> findRegistrationsWithoutQRCode(@Param("eventId") Long eventId);

    @Query("SELECT er FROM EventRegistration er WHERE er.event.eventDate = CURRENT_DATE AND er.status = 'REGISTERED'")
    List<EventRegistration> findTodayRegistrations();
}