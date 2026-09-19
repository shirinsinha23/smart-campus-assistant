package com.example.smartcampusassistant.event.model;

import com.example.smartcampusassistant.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    @Lob
    @Column(name = "certificate_pdf", columnDefinition = "MEDIUMBLOB")
    private byte[] certificatePdf;

    @Column(name = "issue_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime issueDate;

    @Column(name = "certificate_number", unique = true, length = 50)
    private String certificateNumber;

    // ✅ FIX: Add @Builder.Default for default value
    @Builder.Default
    @Column(name = "is_downloaded")
    private Boolean isDownloaded = false;

    @PrePersist
    public void generateCertificateNumber() {
        if (this.certificateNumber == null) {
            this.certificateNumber = "CERT-" + System.currentTimeMillis();
        }
    }
}