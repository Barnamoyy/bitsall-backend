package com.bitsall.model.dto;

import com.bitsall.model.enums.ClubType;
import com.bitsall.model.enums.DepartmentType;
import com.bitsall.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private DepartmentType department;
    private ClubType club;
    private String phoneNumber;
    private LocalDateTime dateJoined;
    private boolean verified;
}

