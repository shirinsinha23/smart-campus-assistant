package com.example.smartcampusassistant.event.repository;

import com.example.smartcampusassistant.event.model.Event;
import com.example.smartcampusassistant.event.model.Event.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e JOIN FETCH e.createdBy LEFT JOIN FETCH e.club WHERE e.id = :id")
    Optional<Event> findByIdWithClubAndUser(@Param("id") Long id);

    @Query("SELECT e FROM Event e JOIN FETCH e.createdBy LEFT JOIN FETCH e.club")
    List<Event> findAllWithClubAndUser();

    @Query("SELECT e FROM Event e JOIN FETCH e.createdBy LEFT JOIN FETCH e.club WHERE e.eventDate >= :date")
    List<Event> findUpcomingEventsWithClubAndUser(@Param("date") LocalDate date);

    @Query("SELECT e FROM Event e JOIN FETCH e.createdBy LEFT JOIN FETCH e.club WHERE e.title LIKE %:keyword% OR e.description LIKE %:keyword% OR e.venue LIKE %:keyword%")
    List<Event> searchEvents(@Param("keyword") String keyword);

    List<Event> findByStatus(EventStatus status);
    List<Event> findByCreatedById(Long userId);
    List<Event> findByClubId(Long clubId);
    Page<Event> findAllByOrderByEventDateAsc(Pageable pageable);

    @Query("SELECT e FROM Event e JOIN e.registeredUsers u WHERE u.id = :userId")
    List<Event> findByRegisteredUsersId(@Param("userId") Long userId);
}