package com.bitsall.model.dto;

import com.bitsall.model.enums.CarpoolType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CarpoolRequestDTO {
    private UUID id;
    private String pickupLocation;
    private String destination;
    private LocalDateTime pickupTime;
    private String phoneNumber;
    private CarpoolType requestType;
    
    @NotNull(message = "User ID is required")
    private UUID createdBy;
}
