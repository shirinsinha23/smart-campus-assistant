package com.example.smartcampusassistant.library;

import lombok.Data;

@Data
public class BorrowingRecord {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private String status;
}