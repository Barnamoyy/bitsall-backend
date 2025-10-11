package com.bitsall.controller;

import com.bitsall.model.entity.PawnItem;
import com.bitsall.service.PawnShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pawnshop")
@RequiredArgsConstructor
public class PawnShopController {

    private final PawnShopService pawnShopService;

    @PostMapping
    public ResponseEntity<PawnItem> createPawnItem(@Valid @RequestBody PawnItem pawnItem) {
        PawnItem createdPawnItem = pawnShopService.createPawnItem(pawnItem);
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
