package com.example.smartcampusassistant.user.dto;

import com.example.smartcampusassistant.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String password;

    private Role role;

    private String phoneNumber;

    private String loginId;

    // ✅ NEW: Stream field
    private String stream;

    // ✅ NEW: Block field
    private String block;
}