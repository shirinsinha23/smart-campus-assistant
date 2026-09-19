// CompanyDTO.java
package com.example.smartcampusassistant.placement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Long id;
    private String name;
    private String industry;
    private String website;
    private String email;
    private String phone;
    private String address;
    private String logoUrl;
    private String description;
    private Boolean isActive;
    private Integer totalDrives;
}