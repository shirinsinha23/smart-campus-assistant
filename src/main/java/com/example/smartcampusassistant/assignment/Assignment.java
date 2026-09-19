package com.example.smartcampusassistant.assignment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String subject;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "max_score", nullable = false)
    @Builder.Default
    private Integer maxScore = 10;

    @Column(nullable = false)
    @Builder.Default
    private Integer submissions = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer graded = 0;

    @Column(name = "faculty_id")
    private Long facultyId;

    @Column(name = "faculty_name", length = 200)
    private String facultyName;

    /** Path to the uploaded question paper PDF, e.g. /uploads/assignments/<uuid>.pdf */
    @Column(name = "question_file_url", length = 500)
    private String questionFileUrl;

    /** Original filename the faculty uploaded */
    @Column(name = "question_file_name", length = 500)
    private String questionFileName;

    /** Optional text description shown alongside the PDF */
    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Transient
    public boolean isOverdue() {
        return deadline != null && deadline.isBefore(LocalDate.now());
    }
}