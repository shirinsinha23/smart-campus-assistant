package com.example.smartcampusassistant.complaint;

import lombok.Data;

@Data
public class ComplaintCommentRequest {
    private String text;
    private String user;
}