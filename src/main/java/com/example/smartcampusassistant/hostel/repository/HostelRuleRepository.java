package com.example.smartcampusassistant.hostel.repository;

import com.example.smartcampusassistant.hostel.entity.HostelRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostelRuleRepository extends JpaRepository<HostelRule, Long> {

    List<HostelRule> findByHostelId(Long hostelId);

    List<HostelRule> findByHostelIdAndIsActiveTrue(Long hostelId);

    List<HostelRule> findByCategory(String category);

    List<HostelRule> findByHostelIdAndCategory(Long hostelId, String category);

    List<HostelRule> findByHostelIdAndIsActiveTrueOrderByPriorityAsc(Long hostelId);

    List<HostelRule> findByIsActiveTrueOrderByPriorityAsc();

    @Query("SELECT hr FROM HostelRule hr WHERE hr.hostel.id = :hostelId AND hr.isActive = true AND hr.priority <= :priority")
    List<HostelRule> findActiveRulesWithPriorityLessThan(@Param("hostelId") Long hostelId,
                                                         @Param("priority") Integer priority);

    @Query("SELECT hr FROM HostelRule hr WHERE hr.hostel.id = :hostelId AND hr.category = :category AND hr.isActive = true")
    List<HostelRule> findActiveRulesByCategory(@Param("hostelId") Long hostelId, @Param("category") String category);

    @Query("SELECT hr FROM HostelRule hr WHERE hr.hostel.id = :hostelId AND hr.ruleText LIKE %:keyword% AND hr.isActive = true")
    List<HostelRule> searchRulesByKeyword(@Param("hostelId") Long hostelId, @Param("keyword") String keyword);

    @Query("SELECT COUNT(hr) FROM HostelRule hr WHERE hr.hostel.id = :hostelId AND hr.isActive = true")
    long countActiveRulesByHostel(@Param("hostelId") Long hostelId);

    @Query("SELECT hr.category, COUNT(hr) FROM HostelRule hr WHERE hr.hostel.id = :hostelId AND hr.isActive = true GROUP BY hr.category")
    List<Object[]> countRulesByCategory(@Param("hostelId") Long hostelId);

    @Query("SELECT hr FROM HostelRule hr WHERE hr.isActive = true")
    List<HostelRule> findAllActiveRules();

    @Query("SELECT hr FROM HostelRule hr WHERE hr.hostel.id = :hostelId AND hr.isActive = true AND hr.category IN :categories")
    List<HostelRule> findActiveRulesByCategories(@Param("hostelId") Long hostelId,
                                                 @Param("categories") List<String> categories);

    @Query("SELECT MAX(hr.priority) FROM HostelRule hr WHERE hr.hostel.id = :hostelId")
    Integer getMaxPriorityByHostel(@Param("hostelId") Long hostelId);
}