package com.bitsall.service;

import com.bitsall.model.entity.PawnItem;
import com.bitsall.repository.PawnItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PawnShopService {

    private final PawnItemRepository pawnItemRepository;

    public PawnItem createPawnItem(PawnItem pawnItem) {
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
