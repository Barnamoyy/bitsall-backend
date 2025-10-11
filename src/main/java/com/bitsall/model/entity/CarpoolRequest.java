package com.bitsall.model.entity;

import com.bitsall.model.enums.CarpoolType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarpoolRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @NotBlank(message = "Destination is required")
    private String destination;

    private LocalDateTime pickupTime;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private CarpoolType requestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
}
