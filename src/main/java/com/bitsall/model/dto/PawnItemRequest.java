package com.bitsall.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PawnItemRequest {
    private UUID id;
    private String productName;
    private String description;
    private Integer yearOfPurchase;
    private Double askingPrice;
    private Boolean negotiable;
    private Boolean sold;
    private List<String> imageUrls;
    
    @NotNull(message = "User ID is required")
    private UUID createdBy;
    
    // Seller information (returned in response, not required in request)
    private String createdByFullName;
    private String createdByPhoneNumber;
}
