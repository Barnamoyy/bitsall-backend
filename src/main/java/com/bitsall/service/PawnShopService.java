package com.bitsall.service;

import com.bitsall.mapper.PawnItemMapper;
import com.bitsall.model.dto.PawnItemRequest;
import com.bitsall.model.entity.PawnItem;
import com.bitsall.repository.PawnItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PawnShopService {

    private final PawnItemRepository pawnItemRepository;
    private final AzureBlobService azureBlobService;
    private final PawnItemMapper pawnItemMapper;

    public PawnItemRequest createPawnItem(PawnItemRequest pawnItemRequest, List<MultipartFile> images) throws IOException {
        PawnItem pawnItem = pawnItemMapper.toEntity(pawnItemRequest);
        pawnItem.setSold(false);

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = images.stream()
                    .map(image -> {
                        try {
                            return azureBlobService.uploadFile(image, "pawn-shop", pawnItem.getProductName());
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
                        }
                    })
                    .toList();
            pawnItem.setImageUrls(imageUrls);
        }
        PawnItem saved = pawnItemRepository.save(pawnItem);
        return pawnItemMapper.toDto(saved);
    }

    public List<PawnItemRequest> getAllPawnItems() {
        return pawnItemRepository.findAll().stream()
                .map(pawnItemMapper::toDto)
                .toList();
    }

    public List<PawnItemRequest> getPawnItemsBySoldStatus(boolean sold) {
        return pawnItemRepository.findBySold(sold).stream()
                .map(pawnItemMapper::toDto)
                .toList();
    }

    public PawnItemRequest getPawnItemById(UUID id) {
        PawnItem pawnItem = pawnItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pawn item not found with id: " + id));
        return pawnItemMapper.toDto(pawnItem);
    }

    public void deletePawnItem(UUID id) {
        if (!pawnItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Pawn item not found with id: " + id);
        }
        pawnItemRepository.deleteById(id);
    }
}
