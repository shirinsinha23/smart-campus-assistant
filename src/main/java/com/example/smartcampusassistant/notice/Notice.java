package com.example.smartcampusassistant.notice;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String priority;
    private boolean pinned;
    private String createdBy;
    private String createdAt;
    private String targetAudience;
}