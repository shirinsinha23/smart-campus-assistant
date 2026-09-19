package com.example.smartcampusassistant.user.dto;

import com.example.smartcampusassistant.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String phoneNumber;
    private String loginId;
    private String stream;   // ✅ NEW
    private String block;    // ✅ NEW
    private LocalDateTime createdAt;
}