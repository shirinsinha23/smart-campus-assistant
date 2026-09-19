package com.example.smartcampusassistant.coursematerial.dto;

import lombok.Data;

@Data
public class CourseMaterialRequest {
    private String title;
    private String description;
    private String subject;
    private String type;
    private String fileName;
    private Long fileSize;
    private String videoUrl;

    // ✅ NEW: Support for YouTube Playlist
    private String playlistUrl;  // YouTube playlist link
}