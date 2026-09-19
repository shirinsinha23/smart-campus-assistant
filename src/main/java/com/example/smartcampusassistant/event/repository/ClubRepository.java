package com.example.smartcampusassistant.event.repository;

import com.example.smartcampusassistant.event.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByName(String name);

    List<Club> findByIsActiveTrue();

    List<Club> findByCategory(String category);

    List<Club> findByCoordinatorId(Long coordinatorId);

    @Query("SELECT c FROM Club c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Club> searchClubs(@Param("keyword") String keyword);

    @Query("SELECT c.category, COUNT(c) FROM Club c GROUP BY c.category")
    List<Object[]> countClubsByCategory();

    @Query("SELECT c, COUNT(e) as eventCount FROM Club c LEFT JOIN c.events e GROUP BY c ORDER BY eventCount DESC")
    List<Object[]> findClubsWithEventCount();
}