package com.bitsall.model.entity;

import com.bitsall.model.enums.ClubType;
import com.bitsall.model.enums.DepartmentType;
import com.bitsall.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "User role is required")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private DepartmentType department;

    @Enumerated(EnumType.STRING)
    private ClubType club;

    private String phoneNumber;

    @CreationTimestamp
    private LocalDateTime dateJoined;

    private boolean verified = false;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Announcement> announcements;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarpoolRequest> carpoolRequests;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PawnItem> pawnItems;
}
