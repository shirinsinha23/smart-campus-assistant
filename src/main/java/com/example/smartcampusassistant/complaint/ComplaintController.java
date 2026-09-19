package com.example.smartcampusassistant.complaint;

import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        System.out.println("📋 Total complaints fetched: " + complaints.size());
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/student/{userId}")
    public ResponseEntity<List<Complaint>> getStudentComplaints(@PathVariable String userId) {
        System.out.println("=========================================");
        System.out.println("📋 STUDENT COMPLAINTS REQUEST");
        System.out.println("📋 UserId parameter: '" + userId + "'");
        System.out.println("=========================================");

        List<Complaint> complaints = new ArrayList<>();

        // ✅ STEP 1: Find the user first
        Optional<User> userOpt = Optional.empty();
        Long userIdAsLong = null;

        try {
            userIdAsLong = Long.parseLong(userId);
            userOpt = userRepository.findById(userIdAsLong);
            System.out.println("📋 Parsed as Long: " + userIdAsLong);
        } catch (NumberFormatException e) {
            System.out.println("📋 Not a number, treating as loginId: " + userId);
            userOpt = userRepository.findByLoginId(userId);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✅ Found user: " + user.getName());
            System.out.println("   - ID: " + user.getId());
            System.out.println("   - Login ID: " + user.getLoginId());
            System.out.println("   - Role: " + user.getRole());

            // ✅ STEP 2: Try multiple ways to find complaints

            // Method 1: Find by user ID as String
            List<Complaint> byId = complaintRepository.findByCreatedById(String.valueOf(user.getId()));
            System.out.println("📋 By user ID (" + user.getId() + "): " + byId.size() + " complaints");

            // Method 2: Find by login ID
            List<Complaint> byLoginId = complaintRepository.findByCreatedById(user.getLoginId());
            System.out.println("📋 By login ID (" + user.getLoginId() + "): " + byLoginId.size() + " complaints");

            // Combine results (remove duplicates)
            complaints.addAll(byId);
            complaints.addAll(byLoginId);

            // Remove duplicates based on ID
            complaints = complaints.stream().distinct().toList();

            // ✅ STEP 3: If still no complaints, try finding all and filtering
            if (complaints.isEmpty()) {
                List<Complaint> allComplaints = complaintRepository.findAll();
                for (Complaint c : allComplaints) {
                    if (c.getCreatedById() != null) {
                        if (c.getCreatedById().equals(String.valueOf(user.getId())) ||
                                c.getCreatedById().equals(user.getLoginId())) {
                            complaints.add(c);
                        }
                    }
                }
                System.out.println("📋 By manual filtering: " + complaints.size() + " complaints");
            }

            System.out.println("📋 Total unique complaints: " + complaints.size());

            // Log each complaint
            for (Complaint c : complaints) {
                System.out.println("   - ID: " + c.getId() + ", Title: " + c.getTitle() +
                        ", CreatedById: " + c.getCreatedById());
            }

        } else {
            System.out.println("❌ User not found for: " + userId);

            // Try direct match as last resort
            complaints = complaintRepository.findByCreatedById(userId);
            System.out.println("📋 Direct match found: " + complaints.size() + " complaints");
        }

        System.out.println("=========================================");
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/faculty")
    public ResponseEntity<List<Complaint>> getFacultyComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        System.out.println("📋 Faculty fetched " + complaints.size() + " complaints");
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Complaint>> getComplaintsByCategory(@PathVariable String category) {
        List<Complaint> complaints = complaintRepository.findByCategory(category);
        System.out.println("📋 Found " + complaints.size() + " complaints in category: " + category);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Complaint>> getComplaintsByStatus(@PathVariable String status) {
        List<Complaint> complaints = complaintRepository.findByStatus(status);
        System.out.println("📋 Found " + complaints.size() + " complaints with status: " + status);
        return ResponseEntity.ok(complaints);
    }

    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@RequestBody ComplaintRequest request) {
        System.out.println("📝 Creating complaint");
        System.out.println("   Title: " + request.getTitle());
        System.out.println("   CreatedBy: " + request.getCreatedBy());
        System.out.println("   CreatedById: " + request.getCreatedById());
        System.out.println("   Category: " + request.getCategory());
        System.out.println("   Priority: " + request.getPriority());

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setPriority(request.getPriority());
        complaint.setStatus("PENDING");
        complaint.setCreatedBy(request.getCreatedBy());
        complaint.setCreatedById(request.getCreatedById());
        complaint.setCreatedAt(request.getCreatedAt());
        complaint.setComments(new ArrayList<>());

        Complaint saved = complaintRepository.save(complaint);
        System.out.println("✅ Complaint created with ID: " + saved.getId());
        System.out.println("   CreatedById: " + saved.getCreatedById());
        System.out.println("   CreatedBy: " + saved.getCreatedBy());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Complaint> updateStatus(@PathVariable Long id, @RequestBody ComplaintStatusUpdate update) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setStatus(update.getStatus());
        Complaint updated = complaintRepository.save(complaint);
        System.out.println("✅ Complaint " + id + " status updated to: " + update.getStatus());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Complaint> addComment(@PathVariable Long id, @RequestBody ComplaintCommentRequest request) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        ComplaintComment comment = new ComplaintComment();
        comment.setUser(request.getUser());
        comment.setText(request.getText());
        comment.setCreatedAt(LocalDate.now().toString());

        if (complaint.getComments() == null) {
            complaint.setComments(new ArrayList<>());
        }
        complaint.getComments().add(comment);
        Complaint updated = complaintRepository.save(complaint);
        System.out.println("✅ Comment added to complaint " + id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintRepository.deleteById(id);
        System.out.println("✅ Complaint " + id + " deleted");
        return ResponseEntity.ok().build();
    }
}