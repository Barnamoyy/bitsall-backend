package com.bitsall.model.dto;

import com.bitsall.model.enums.DepartmentType;
import com.bitsall.model.enums.VisibilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AnnouncementRequest {
    private UUID id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Body is required")
    private String body;
    
    private LocalDateTime datePosted;
    private LocalDateTime expiryDate;
    private List<String> links;
    private VisibilityType visibility;
    private DepartmentType department;
    private String imageUrl;
    
    @NotNull(message = "User ID is required")
    private UUID createdBy;
}

