// CompanyRepository.java
package com.example.smartcampusassistant.placement.repository;

import com.example.smartcampusassistant.placement.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByIsActiveTrue();
    List<Company> findByIndustry(String industry);

    @Query("SELECT c FROM Company c WHERE c.isActive = true ORDER BY c.name ASC")
    List<Company> findAllActiveOrderedByName();
}