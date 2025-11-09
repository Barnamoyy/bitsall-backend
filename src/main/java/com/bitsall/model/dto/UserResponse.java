package com.bitsall.model.dto;

import com.bitsall.model.enums.ClubType;
import com.bitsall.model.enums.DepartmentType;
import com.bitsall.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private UserRole role;
    private List<DepartmentType> departments;
    private List<ClubType> clubs;
    private String phoneNumber;
    private LocalDateTime dateJoined;
    private boolean verified;
}

