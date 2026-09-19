package com.example.smartcampusassistant.library;

import lombok.Data;

@Data
public class Reservation {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String reservedDate;
    private String status;
}