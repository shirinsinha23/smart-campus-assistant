package com.example.smartcampusassistant.assignment;

import com.example.smartcampusassistant.exception.BadRequestException;
import com.example.smartcampusassistant.exception.ResourceNotFoundException;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Assignment create(Long userId, Assignment incoming) {
        if (incoming.getTitle() == null || incoming.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Title is required");
        }
        if (incoming.getSubject() == null || incoming.getSubject().trim().isEmpty()) {
            throw new BadRequestException("Subject is required");
        }
        if (incoming.getDeadline() == null) {
            throw new BadRequestException("Deadline is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        incoming.setId(null); // force insert
        incoming.setFacultyId(userId);
        incoming.setFacultyName(user.getName());
        if (incoming.getMaxScore() == null || incoming.getMaxScore() <= 0) {
            incoming.setMaxScore(10);
        }
        if (incoming.getSubmissions() == null) incoming.setSubmissions(0);
        if (incoming.getGraded() == null) incoming.setGraded(0);

        return assignmentRepository.save(incoming);
    }

    public List<Assignment> listAll() {
        return assignmentRepository.findAllByOrderByDeadlineAsc();
    }

    public List<Assignment> listByFaculty(Long facultyId) {
        return assignmentRepository.findByFacultyIdOrderByCreatedAtDesc(facultyId);
    }

    @Transactional
    public void delete(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assignment not found: " + id);
        }
        assignmentRepository.deleteById(id);
    }
}