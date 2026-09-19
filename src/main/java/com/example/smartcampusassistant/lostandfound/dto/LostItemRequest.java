package com.example.smartcampusassistant.lostandfound.dto;

import lombok.Data;

@Data
public class LostItemRequest {
    private String title;
    private String description;
    private String category;
    private String location;
    private String date;
    private String status;
    private String contactInfo;
}