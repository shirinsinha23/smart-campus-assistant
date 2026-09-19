package com.example.smartcampusassistant.notice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*")
public class NoticeController {

    private List<Notice> notices = new ArrayList<>(Arrays.asList(
            new Notice(1L, "📢 College Annual Fest 2026", "The annual college fest will be held on August 25-27. Register before August 20.", "College Announcements", "HIGH", true, "Admin", "2026-08-14", "All"),
            new Notice(2L, "📝 Exam Schedule Released", "Mid-term exams will start from September 1. Check the detailed schedule on the portal.", "Exam Notifications", "HIGH", true, "Admin", "2026-08-13", "Students"),
            new Notice(3L, "🎉 Holiday Notice - Independence Day", "College will remain closed on August 15 for Independence Day celebrations.", "Holiday Notices", "MEDIUM", false, "Admin", "2026-08-12", "All"),
            new Notice(4L, "💻 Department Meeting - CSE", "All CSE faculty members are requested to attend the department meeting on August 20 at 3 PM.", "Department Notices", "MEDIUM", false, "Shirin Sinha", "2026-08-11", "Faculty"),
            new Notice(5L, "🎓 Workshop on AI/ML", "A 2-day workshop on Artificial Intelligence and Machine Learning will be conducted on August 28-29.", "Event Updates", "LOW", false, "Anurag Gupta", "2026-08-10", "Students")
    ));
    private Long idCounter = 6L;

    @GetMapping
    public ResponseEntity<List<Notice>> getAllNotices() {
        return ResponseEntity.ok(notices);
    }

    @PostMapping
    public ResponseEntity<Notice> createNotice(@RequestBody Notice notice) {
        notice.setId(idCounter++);
        notices.add(notice);
        return ResponseEntity.ok(notice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        notices.removeIf(n -> n.getId().equals(id));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/pin")
    public ResponseEntity<Notice> pinNotice(@PathVariable Long id) {
        notices.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .ifPresent(n -> n.setPinned(!n.isPinned()));
        return ResponseEntity.ok(notices.stream().filter(n -> n.getId().equals(id)).findFirst().orElse(null));
    }
}