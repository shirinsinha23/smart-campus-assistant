package com.example.smartcampusassistant.library;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "*")
public class LibraryController {

    private List<Book> books = new ArrayList<>(Arrays.asList(
            new Book(1L, "Database Management Systems", "Raghu Ramakrishnan", "978-0072465631", "Textbook", "Section A-3", 5, 3),
            new Book(2L, "Operating System Concepts", "Abraham Silberschatz", "978-1118063330", "Textbook", "Section A-1", 4, 1),
            new Book(3L, "Computer Networks", "Andrew S. Tanenbaum", "978-0132126953", "Textbook", "Section B-2", 3, 0),
            new Book(4L, "Introduction to Algorithms", "Thomas H. Cormen", "978-0262033848", "Reference", "Section C-1", 2, 2),
            new Book(5L, "Clean Code", "Robert C. Martin", "978-0132350884", "Non-Fiction", "Section D-3", 3, 2),
            new Book(6L, "The Pragmatic Programmer", "David Thomas", "978-0201616224", "Non-Fiction", "Section D-1", 2, 1)
    ));

    private Map<String, List<BorrowingRecord>> borrowingHistory = new HashMap<>();
    private Map<String, List<Reservation>> reservations = new HashMap<>();
    private Long idCounter = 7L;

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(books);
    }

    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        book.setId(idCounter++);
        books.add(book);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        books.removeIf(b -> b.getId().equals(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reserve/{bookId}")
    public ResponseEntity<String> reserveBook(@PathVariable Long bookId) {
        books.stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst()
                .ifPresent(book -> {
                    if (book.getAvailableCopies() > 0) {
                        book.setAvailableCopies(book.getAvailableCopies() - 1);
                    }
                });
        return ResponseEntity.ok("Book reserved successfully");
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<BorrowingRecord>> getBorrowingHistory(@PathVariable String userId) {
        List<BorrowingRecord> history = borrowingHistory.getOrDefault(userId, new ArrayList<>());
        if (history.isEmpty()) {
            // Add some mock data
            history = Arrays.asList(
                    createBorrowingRecord(1L, "Database Management Systems", "2026-08-01", "2026-08-15", null, "BORROWED"),
                    createBorrowingRecord(3L, "Computer Networks", "2026-07-20", "2026-08-03", "2026-08-05", "RETURNED")
            );
            borrowingHistory.put(userId, new ArrayList<>(history));
        }
        return ResponseEntity.ok(history);
    }

    @GetMapping("/reservations/{userId}")
    public ResponseEntity<List<Reservation>> getReservations(@PathVariable String userId) {
        List<Reservation> userReservations = reservations.getOrDefault(userId, new ArrayList<>());
        if (userReservations.isEmpty()) {
            userReservations = Arrays.asList(
                    createReservation(3L, "Computer Networks", "2026-08-10", "PENDING")
            );
            reservations.put(userId, new ArrayList<>(userReservations));
        }
        return ResponseEntity.ok(userReservations);
    }

    private BorrowingRecord createBorrowingRecord(Long bookId, String title, String borrowDate, String dueDate, String returnDate, String status) {
        BorrowingRecord record = new BorrowingRecord();
        record.setId((long) (borrowingHistory.size() + 1));
        record.setBookId(bookId);
        record.setBookTitle(title);
        record.setBorrowDate(borrowDate);
        record.setDueDate(dueDate);
        record.setReturnDate(returnDate);
        record.setStatus(status);
        return record;
    }

    private Reservation createReservation(Long bookId, String title, String date, String status) {
        Reservation reservation = new Reservation();
        reservation.setId((long) (reservations.size() + 1));
        reservation.setBookId(bookId);
        reservation.setBookTitle(title);
        reservation.setReservedDate(date);
        reservation.setStatus(status);
        return reservation;
    }
}