package com.example.smartcampusassistant.library;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String location;
    private int totalCopies;
    private int availableCopies;
}