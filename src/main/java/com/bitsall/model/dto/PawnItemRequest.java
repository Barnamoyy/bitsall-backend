package com.bitsall.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PawnItemRequest {
    private String productName;
    private String description;
    private Integer yearOfPurchase;
    private Double askingPrice;
    private Boolean negotiable;
    private List<String> imageUrls;
    private UserRequest createdBy;
}
