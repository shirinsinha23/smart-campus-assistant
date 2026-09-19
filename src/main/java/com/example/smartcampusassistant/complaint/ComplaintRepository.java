package com.example.smartcampusassistant.complaint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Basic find methods
    List<Complaint> findByCategory(String category);
    List<Complaint> findByStatus(String status);
    List<Complaint> findByCreatedById(String createdById);

    // ✅ NEW: Find by createdById with multiple approaches
    @Query("SELECT c FROM Complaint c WHERE c.createdById = :id OR c.createdById = :loginId")
    List<Complaint> findByIdOrLoginId(@Param("id") String id, @Param("loginId") String loginId);

    // ✅ NEW: Find complaints for a specific user by their numeric ID
    @Query("SELECT c FROM Complaint c WHERE c.createdById = CAST(:userId AS string)")
    List<Complaint> findByUserId(@Param("userId") Long userId);

    // ✅ NEW: Find complaints where createdById matches any of the given IDs
    @Query("SELECT c FROM Complaint c WHERE c.createdById IN :ids")
    List<Complaint> findByCreatedByIdIn(@Param("ids") List<String> ids);

    // ✅ NEW: Count complaints by createdById
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.createdById = :createdById")
    long countByCreatedById(@Param("createdById") String createdById);
}