package com.bitsall.model.dto;

import com.bitsall.model.enums.ClubType;
import com.bitsall.model.enums.DepartmentType;
import com.bitsall.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class UserRequest {
    private UUID id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "User role is required")
    private UserRole role;

    private List<DepartmentType> departments;

    private List<ClubType> clubs;

    private String phoneNumber;
}
