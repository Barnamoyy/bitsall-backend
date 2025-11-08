package com.bitsall.controller;

import com.bitsall.model.dto.PawnItemRequest;
import com.bitsall.model.dto.SuccessResponse;
import com.bitsall.service.PawnShopService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pawnshop")
@RequiredArgsConstructor
@Validated
public class PawnShopController {

    private final PawnShopService pawnShopService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PawnItemRequest> createPawnItem(
            @RequestParam(value = "pawnItem", required = true) String pawnItemJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        PawnItemRequest pawnItemRequest;
        try {
            pawnItemRequest = objectMapper.readValue(pawnItemJson, PawnItemRequest.class);
        } catch (com.fasterxml.jackson.databind.exc.InvalidFormatException e) {
            if (e.getMessage().contains("UUID")) {
                throw new IllegalArgumentException("Invalid UUID format in request. Please provide a valid UUID (e.g., 'c0b77917-8f23-483b-9e71-9bd03424f996')", e);
            }
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }
        
        // Validate the parsed request
        jakarta.validation.Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
        var violations = validator.validate(pawnItemRequest);
        if (!violations.isEmpty()) {
            throw new jakarta.validation.ConstraintViolationException(violations);
        }
        PawnItemRequest createdPawnItem = pawnShopService.createPawnItem(pawnItemRequest, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPawnItem);
    }

    @GetMapping
    public ResponseEntity<List<PawnItemRequest>> getAllPawnItems(@RequestParam(required = false) Boolean sold) {
        List<PawnItemRequest> pawnItems = (sold != null)
                ? pawnShopService.getPawnItemsBySoldStatus(sold)
                : pawnShopService.getAllPawnItems();
        return ResponseEntity.ok(pawnItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PawnItemRequest> getPawnItemById(@PathVariable UUID id) {
        PawnItemRequest pawnItem = pawnShopService.getPawnItemById(id);
        return ResponseEntity.ok(pawnItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deletePawnItem(@PathVariable UUID id) {
        pawnShopService.deletePawnItem(id);
        return ResponseEntity.ok(new SuccessResponse());
    }
}
