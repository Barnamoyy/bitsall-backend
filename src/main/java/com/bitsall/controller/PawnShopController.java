package com.bitsall.controller;

import com.bitsall.model.dto.PawnItemRequest;
import com.bitsall.model.entity.PawnItem;
import com.bitsall.service.PawnShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pawnshop")
@RequiredArgsConstructor
public class PawnShopController {

    private final PawnShopService pawnShopService;

    @PostMapping
    public ResponseEntity<PawnItem> createPawnItem(@Valid @RequestPart("pawnItem") PawnItemRequest pawnItemRequest, @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        PawnItem createdPawnItem = pawnShopService.createPawnItem(pawnItemRequest, images);
        return new ResponseEntity<>(createdPawnItem, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PawnItem>> getAllPawnItems(@RequestParam(required = false) Boolean sold) {
        List<PawnItem> pawnItems;
        if (sold != null) {
            pawnItems = pawnShopService.getPawnItemsBySoldStatus(sold);
        } else {
            pawnItems = pawnShopService.getAllPawnItems();
        }
        return new ResponseEntity<>(pawnItems, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePawnItem(@PathVariable Long id) {
        pawnShopService.deletePawnItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
