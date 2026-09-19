package com.example.smartcampusassistant.complaint;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "complaint_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user;
    private String text;
    private String createdAt;
}