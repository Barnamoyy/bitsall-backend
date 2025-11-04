package com.bitsall.model.dto;

import com.bitsall.model.enums.CarpoolType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarpoolRequestDTO {
    private String pickupLocation;
    private String destination;
    private java.time.LocalDateTime pickupTime;
    private String phoneNumber;
    private CarpoolType requestType;
    private UserRequest createdBy;
}
