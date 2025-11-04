package com.bitsall.service;

import com.bitsall.mapper.PawnItemMapper;
import com.bitsall.model.dto.PawnItemRequest;
import com.bitsall.model.entity.PawnItem;
import com.bitsall.repository.PawnItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PawnShopService {

    private PawnItemRepository pawnItemRepository;
    private AzureBlobService azureBlobService;
    private PawnItemMapper pawnItemMapper;

    public PawnItem createPawnItem(PawnItemRequest pawnItemRequest, List<MultipartFile> images) throws IOException {
        PawnItem pawnItem = pawnItemMapper.toEntity(pawnItemRequest);
        pawnItem.setSold(false);

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                String imageUrl = azureBlobService.uploadFile(image, "pawn-shop", pawnItem.getProductName());
                imageUrls.add(imageUrl);
            }
            pawnItem.setImageUrls(imageUrls);
        }
        return pawnItemRepository.save(pawnItem);
    }

    public List<PawnItem> getAllPawnItems() {
        return pawnItemRepository.findAll();
    }

    public List<PawnItem> getPawnItemsBySoldStatus(boolean sold) {
        return pawnItemRepository.findBySold(sold);
    }

    public void deletePawnItem(Long id) {
        pawnItemRepository.deleteById(id);
    }
}
