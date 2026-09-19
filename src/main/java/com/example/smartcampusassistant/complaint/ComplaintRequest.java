package com.example.smartcampusassistant.complaint;

import lombok.Data;

@Data
public class ComplaintRequest {
    private String title;
    private String description;
    private String category;
    private String priority;
    private String createdBy;
    private String createdById;
    private String createdAt;
}