package com.bitsall.model.entity;

import com.bitsall.model.enums.DepartmentType;
import com.bitsall.model.enums.VisibilityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    @Lob
    private String body;

    @CreationTimestamp
    private LocalDateTime datePosted;

    private LocalDateTime expiryDate;

    @ElementCollection
    private List<String> links;

    @Enumerated(EnumType.STRING)
    private VisibilityType visibility;

    @Enumerated(EnumType.STRING)
    private DepartmentType department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
}
