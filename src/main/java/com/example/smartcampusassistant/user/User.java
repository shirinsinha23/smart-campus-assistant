package com.example.smartcampusassistant.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String phoneNumber;

    @Column(name = "login_id", unique = true, length = 10)
    private String loginId;

    @Column(name = "otp")
    @JsonIgnore
    private String otp;

    @Column(name = "otp_expiry")
    @JsonIgnore
    private LocalDateTime otpExpiry;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "stream")
    private String stream;

    @Column(name = "block")
    private String block;

    // 👇 ADD THIS FIELD
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.loginId == null) {
            generateLoginId();
        }
        // ✅ Ensure isActive is set
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    public void generateLoginId() {
        if (this.role == Role.STUDENT) {
            this.loginId = "10" + String.format("%02d", (int)(Math.random() * 100) + 1);
        } else if (this.role == Role.FACULTY) {
            this.loginId = "20" + String.format("%04d", (int)(Math.random() * 10000) + 1);
        } else if (this.role == Role.ADMIN) {
            this.loginId = "30" + String.format("%04d", (int)(Math.random() * 10000) + 1);
        }
    }

    public void generateOtp() {
        // Reuse existing OTP if it's still valid
        if (this.otp != null && this.otpExpiry != null && this.otpExpiry.isAfter(LocalDateTime.now())) {
            System.out.println("=========================================");
            System.out.println("♻️ Reusing existing OTP for " + this.loginId + " (" + this.email + "): " + this.otp);
            System.out.println("⏰ Still valid until: " + this.otpExpiry);
            System.out.println("=========================================");
            return;
        }

        // Otherwise generate a new OTP valid for 24 hours
        this.otp = String.format("%06d", (int)(Math.random() * 1000000));
        this.otpExpiry = LocalDateTime.now().plusHours(24);
        System.out.println("=========================================");
        System.out.println("🔐 New OTP for " + this.loginId + " (" + this.email + "): " + this.otp);
        System.out.println("⏰ Valid for 24 hours until: " + this.otpExpiry);
        System.out.println("=========================================");
    }

    public boolean isOtpValid(String inputOtp) {
        return this.otp != null &&
                this.otp.equals(inputOtp) &&
                this.otpExpiry != null &&
                this.otpExpiry.isAfter(LocalDateTime.now());
    }

    public void clearOtp() {
        this.otp = null;
        this.otpExpiry = null;
    }
}