package com.example.smartcampusassistant.transport.repository;

import com.example.smartcampusassistant.transport.entity.BusRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {

    Optional<BusRoute> findByRouteName(String routeName);

    List<BusRoute> findByIsActiveTrue();

    List<BusRoute> findByRouteType(String routeType);

    @Query("SELECT r FROM BusRoute r WHERE LOWER(r.startLocation) LIKE LOWER(CONCAT('%', :location, '%')) " +
            "OR LOWER(r.endLocation) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<BusRoute> findByLocation(@Param("location") String location);
}