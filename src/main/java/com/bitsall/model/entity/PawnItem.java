package com.bitsall.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PawnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Description is required")
    @Lob
    private String description;

    @NotNull(message = "Year of purchase is required")
    private Integer yearOfPurchase;

    @NotNull(message = "Asking price is required")
    @Min(value = 0, message = "Price must be positive")
    private Double askingPrice;

    private boolean negotiable = false;

    private boolean sold = false;

    @ElementCollection
    @Size(min = 1, max = 3, message = "Must have between 1 and 3 image URLs")
    private List<String> imageUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
}
