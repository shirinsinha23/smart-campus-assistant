package com.example.smartcampusassistant.cafeteria.model;

import com.example.smartcampusassistant.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cafeteria_feedbacks")  // ← Changed from "feedbacks" to "cafeteria_feedbacks"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CafeteriaFeedback {  // ← Renamed class to avoid confusion

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private com.example.smartcampusassistant.cafeteria.model.Order order;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(length = 500)
    private String comment;

    @Column(name = "meal_item_id")
    private Long mealItemId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}