package com.bitsall.service;

import com.bitsall.model.dto.PawnItemRequest;
import com.bitsall.model.entity.PawnItem;
import com.bitsall.model.entity.User;
import com.bitsall.repository.PawnItemRepository;
import com.bitsall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PawnShopService {

    private final PawnItemRepository pawnItemRepository;
    private final AzureBlobService azureBlobService;
    private final UserRepository userRepository;

    public PawnItemRequest createPawnItem(PawnItemRequest pawnItemRequest, List<MultipartFile> images) throws IOException {
        User user = userRepository.findById(pawnItemRequest.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + pawnItemRequest.getCreatedBy()));

        PawnItem pawnItem = PawnItem.builder()
                .productName(pawnItemRequest.getProductName())
                .description(pawnItemRequest.getDescription())
                .yearOfPurchase(pawnItemRequest.getYearOfPurchase())
                .askingPrice(pawnItemRequest.getAskingPrice())
                .negotiable(pawnItemRequest.getNegotiable() != null ? pawnItemRequest.getNegotiable() : false)
                .sold(false)
                .createdBy(user)
                .build();

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = images.stream()
                    .map(image -> {
                        try {
                            return azureBlobService.uploadFile(image, "pawn-shop", pawnItem.getProductName());
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
                        }
                    })
                    .collect(Collectors.toList());
            pawnItem.setImageUrls(imageUrls);
        }
        PawnItem saved = pawnItemRepository.save(pawnItem);
        return toDto(saved);
    }

    public List<PawnItemRequest> getAllPawnItems() {
        return pawnItemRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PawnItemRequest> getPawnItemsBySoldStatus(boolean sold) {
        return pawnItemRepository.findBySold(sold).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PawnItemRequest getPawnItemById(UUID id) {
        PawnItem pawnItem = pawnItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pawn item not found with id: " + id));
        return toDto(pawnItem);
    }

    private PawnItemRequest toDto(PawnItem pawnItem) {
        PawnItemRequest dto = new PawnItemRequest();
        dto.setId(pawnItem.getId());
        dto.setProductName(pawnItem.getProductName());
        dto.setDescription(pawnItem.getDescription());
        dto.setYearOfPurchase(pawnItem.getYearOfPurchase());
        dto.setAskingPrice(pawnItem.getAskingPrice());
        dto.setNegotiable(pawnItem.isNegotiable());
        dto.setSold(pawnItem.isSold());
        dto.setImageUrls(pawnItem.getImageUrls());
        dto.setCreatedBy(pawnItem.getCreatedBy() != null ? pawnItem.getCreatedBy().getId() : null);
        dto.setCreatedByFullName(pawnItem.getCreatedBy() != null ? pawnItem.getCreatedBy().getFullName() : null);
        dto.setCreatedByPhoneNumber(pawnItem.getCreatedBy() != null ? pawnItem.getCreatedBy().getPhoneNumber() : null);
        return dto;
    }

    public void deletePawnItem(UUID id) {
        if (!pawnItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Pawn item not found with id: " + id);
        }
        pawnItemRepository.deleteById(id);
    }
}
