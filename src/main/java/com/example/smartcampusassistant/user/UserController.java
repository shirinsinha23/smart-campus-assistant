package com.example.smartcampusassistant.user;

import com.example.smartcampusassistant.security.CustomUserDetails;
import com.example.smartcampusassistant.security.JwtService;
import com.example.smartcampusassistant.security.SecurityUtils;
import com.example.smartcampusassistant.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // ==================== AUTH ENDPOINTS ====================

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            System.out.println("=== REGISTRATION REQUEST ===");
            System.out.println("Email: " + request.getEmail());
            System.out.println("Name: " + request.getName());
            System.out.println("Role: " + request.getRole());

            if (request.getRole() == null) {
                request.setRole(Role.STUDENT);
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Email already registered. Please use a different email.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                request.setPassword("user123");
            }

            User user = userService.register(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("loginId", user.getLoginId());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== OTP LOGIN (No Password) ====================

    @PostMapping("/auth/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpRequest request) {
        try {
            System.out.println("=== SEND OTP REQUEST ===");
            System.out.println("Login ID: " + request.getLoginId());

            User user = userService.generateAndSendOtp(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent successfully to your registered email");
            response.put("loginId", user.getLoginId());
            response.put("email", user.getEmail());
            response.put("expiresIn", "24 hours");
            response.put("otp", user.getOtp());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            System.out.println("=== LOGIN REQUEST ===");
            System.out.println("Login ID: " + request.getLoginId());
            System.out.println("OTP: " + request.getOtp());

            User user = userService.loginWithOtp(request);

            // ✅ Generate token using CustomUserDetails
            CustomUserDetails userDetails = new CustomUserDetails(user);
            String token = jwtService.generateToken(userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("loginId", user.getLoginId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== USER MANAGEMENT ENDPOINTS ====================

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        System.out.println("📋 Found " + users.size() + " total users");
        return ResponseEntity.ok(users.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/users/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<UserResponse>> getAllStudents() {
        List<User> students = userService.getAllStudents();
        System.out.println("📋 Found " + students.size() + " students");
        for (User u : students) {
            System.out.println("   - ID: " + u.getId() + ", Name: " + u.getName() + ", LoginId: " + u.getLoginId());
        }
        return ResponseEntity.ok(students.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/users/faculty")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllFaculty() {
        List<User> faculty = userService.getAllFaculty();
        System.out.println("📋 Found " + faculty.size() + " faculty members");
        for (User u : faculty) {
            System.out.println("   - ID: " + u.getId() + ", Name: " + u.getName() + ", LoginId: " + u.getLoginId());
        }
        return ResponseEntity.ok(faculty.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/users/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserCountResponse> getUserCounts() {
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long totalFaculty = userRepository.countByRole(Role.FACULTY);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);

        System.out.println("📊 User Counts - Total: " + totalUsers +
                ", Students: " + totalStudents +
                ", Faculty: " + totalFaculty +
                ", Admins: " + totalAdmins);

        return ResponseEntity.ok(UserCountResponse.builder()
                .totalUsers(totalUsers)
                .totalStudents(totalStudents)
                .totalFaculty(totalFaculty)
                .totalAdmins(totalAdmins)
                .build());
    }

    @GetMapping("/users/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getRecentUsers() {
        List<User> recentUsers = userRepository.findTop5ByOrderByCreatedAtDesc();
        System.out.println("📋 Found " + recentUsers.size() + " recent users");
        return ResponseEntity.ok(recentUsers.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList()));
    }

    // ==================== ADMIN: ADD STUDENTS & FACULTY (No Password Required) ====================

    @PostMapping("/users/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addStudent(@RequestBody RegisterRequest request) {
        try {
            System.out.println("📝 Adding new student: " + request.getName());
            User user = userService.addStudent(request);
            System.out.println("✅ Student added with ID: " + user.getId() + ", LoginId: " + user.getLoginId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Student added successfully",
                            "userId", user.getId(),
                            "loginId", user.getLoginId(),
                            "email", user.getEmail()
                    ));
        } catch (Exception e) {
            System.err.println("❌ Failed to add student: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to add student: " + e.getMessage()));
        }
    }

    @PostMapping("/users/faculty")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addFaculty(@RequestBody RegisterRequest request) {
        try {
            System.out.println("📝 Adding new faculty: " + request.getName());
            User user = userService.addFaculty(request);
            System.out.println("✅ Faculty added with ID: " + user.getId() + ", LoginId: " + user.getLoginId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Faculty added successfully",
                            "userId", user.getId(),
                            "loginId", user.getLoginId(),
                            "email", user.getEmail()
                    ));
        } catch (Exception e) {
            System.err.println("❌ Failed to add faculty: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to add faculty: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody RegisterRequest request) {
        try {
            System.out.println("📝 Updating user with ID: " + id);
            User user = userService.updateUser(id, request);
            System.out.println("✅ User updated: " + user.getName() + " (" + user.getLoginId() + ")");
            return ResponseEntity.ok(Map.of(
                    "message", "User updated successfully",
                    "userId", user.getId(),
                    "loginId", user.getLoginId()
            ));
        } catch (Exception e) {
            System.err.println("❌ Failed to update user: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            System.out.println("🗑️ Deleting user with ID: " + id);
            userService.deleteUser(id);
            System.out.println("✅ User deleted successfully");
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            System.err.println("❌ Failed to delete user: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    // ==================== PRIVATE METHODS ====================

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .loginId(user.getLoginId())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<?> verifyToken() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            System.out.println("🔐 Token verified for user ID: " + userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Token is valid",
                    "userId", userId
            ));
        } catch (Exception e) {
            System.err.println("❌ Token verification failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token: " + e.getMessage()));
        }
    }
}