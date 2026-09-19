package com.example.smartcampusassistant.event.model;

import com.example.smartcampusassistant.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_registrations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "registration_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.REGISTERED;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "qr_code", length = 500)
    private String qrCode;

    @Column(name = "qr_code_image", columnDefinition = "LONGTEXT")
    private String qrCodeImage;

    public enum RegistrationStatus {
        REGISTERED,
        CHECKED_IN,
        CANCELLED,
        WAITLISTED
    }
}