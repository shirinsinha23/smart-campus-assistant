package com.example.smartcampusassistant.lostandfound.repository;

import com.example.smartcampusassistant.lostandfound.entity.ItemStatus;
import com.example.smartcampusassistant.lostandfound.entity.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {

    List<LostItem> findByStatus(ItemStatus status);

    List<LostItem> findByReportedById(Long userId);

    List<LostItem> findByCategory(String category);

    List<LostItem> findByCategoryAndStatus(String category, ItemStatus status);

    @Query("SELECT i FROM LostItem i WHERE " +
            "LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<LostItem> searchByKeyword(@Param("keyword") String keyword);
}