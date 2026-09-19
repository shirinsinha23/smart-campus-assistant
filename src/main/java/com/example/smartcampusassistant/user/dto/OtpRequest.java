package com.example.smartcampusassistant.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequest {

    @NotBlank(message = "Login ID is required")
    private String loginId;
}