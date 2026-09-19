package com.example.smartcampusassistant.complaint;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category; // Hostel, Library, Classroom, Internet, Transport, Cafeteria

    @Column(nullable = false)
    private String status; // PENDING, IN_PROGRESS, RESOLVED, REJECTED

    private String priority; // HIGH, MEDIUM, LOW

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_by_id")
    private String createdById;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "assigned_to")
    private String assignedTo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "complaint_id")
    private List<ComplaintComment> comments = new ArrayList<>();
}