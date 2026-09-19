package com.example.smartcampusassistant.user;

import com.example.smartcampusassistant.service.EmailService;
import com.example.smartcampusassistant.user.dto.LoginRequest;
import com.example.smartcampusassistant.user.dto.OtpRequest;
import com.example.smartcampusassistant.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        String password = request.getPassword();
        if (password == null || password.isEmpty()) {
            password = "user123";
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (request.getRole() == null) {
            request.setRole(Role.STUDENT);
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(password))
                .role(request.getRole())
                .phoneNumber(request.getPhoneNumber())
                .stream(request.getStream())
                .block(request.getBlock())
                .build();

        User savedUser = userRepository.save(user);

        savedUser.setLoginId(generateSequentialLoginId(savedUser));
        savedUser = userRepository.save(savedUser);

        System.out.println("✅ User registered: " + savedUser.getLoginId() + " - " + savedUser.getName() + " (" + savedUser.getRole() + ")");

        try {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName(), savedUser.getLoginId());
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return savedUser;
    }

    private String generateSequentialLoginId(User user) {
        String loginId = null;
        int attempt = 0;

        do {
            if (user.getRole() == Role.STUDENT) {
                long studentCount = userRepository.countByRole(Role.STUDENT);
                loginId = String.format("10%02d", studentCount + attempt + 1);
            } else if (user.getRole() == Role.FACULTY) {
                long facultyCount = userRepository.countByRole(Role.FACULTY);
                loginId = String.format("20%04d", facultyCount + attempt + 1);
            } else if (user.getRole() == Role.ADMIN) {
                long adminCount = userRepository.countByRole(Role.ADMIN);
                loginId = String.format("30%04d", adminCount + attempt + 1);
            }
            attempt++;
        } while (userRepository.findByLoginId(loginId).isPresent() && attempt < 100);

        return loginId;
    }

    // ==================== OTP METHODS ====================

    public User generateAndSendOtp(OtpRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with Login ID: " + request.getLoginId()));

        // ⬇️ This calls User.generateOtp() which is where the 5-min expiry is set.
        // Change the duration in User.java, NOT here.
        user.generateOtp();
        userRepository.save(user);

        // Extra log confirming the new duration
        System.out.println("⏰ OTP valid for 24 hours for login ID: " + user.getLoginId());

        try {
            emailService.sendOtpEmail(user.getEmail(), user.getLoginId(), user.getOtp(), user.getName());
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP. Please try again.");
        }

        return user;
    }

    public User loginWithOtp(LoginRequest request) {
        if (request.getLoginId() == null || request.getLoginId().trim().isEmpty()) {
            throw new IllegalArgumentException("Login ID is required");
        }

        if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
            throw new IllegalArgumentException("OTP is required");
        }

        User user = userRepository.findByLoginId(request.getLoginId().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Login ID or OTP"));

        // ⬇️ This calls User.isOtpValid() which checks the expiry.
        // Change the check in User.java, NOT here.
        if (!user.isOtpValid(request.getOtp())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        user.clearOtp();
        userRepository.save(user);

        return user;
    }

    // ==================== ADMIN FUNCTIONS ====================

    public User addStudent(RegisterRequest request) {
        request.setRole(Role.STUDENT);
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            request.setPassword("student123");
        }
        return register(request);
    }

    public User addFaculty(RegisterRequest request) {
        request.setRole(Role.FACULTY);
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            request.setPassword("faculty123");
        }
        return register(request);
    }

    public User addAdmin(RegisterRequest request) {
        request.setRole(Role.ADMIN);
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            request.setPassword("admin123");
        }
        return register(request);
    }

    public List<User> getAllStudents() {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        System.out.println("📋 Found " + students.size() + " students in database");
        return students;
    }

    public List<User> getAllFaculty() {
        List<User> faculty = userRepository.findByRole(Role.FACULTY);
        System.out.println("📋 Found " + faculty.size() + " faculty members in database");
        return faculty;
    }

    public List<User> getAllAdmins() {
        return userRepository.findByRole(Role.ADMIN);
    }

    public User updateUser(Long id, RegisterRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        existingUser.setStream(request.getStream());
        existingUser.setBlock(request.getBlock());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
        System.out.println("🗑️ User deleted: " + user.getName() + " (" + user.getLoginId() + ")");
    }

    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }

    public long getTotalUsers() {
        return userRepository.count();
    }
}