package com.bitsall.controller;

import com.bitsall.model.dto.CarpoolRequestDTO;
import com.bitsall.model.dto.SuccessResponse;
import com.bitsall.service.CarpoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carpool")
@RequiredArgsConstructor
public class CarpoolController {

    private final CarpoolService carpoolService;

    @PostMapping
    public ResponseEntity<CarpoolRequestDTO> createCarpoolRequest(@Valid @RequestBody CarpoolRequestDTO carpoolRequestDTO) {
        CarpoolRequestDTO createdCarpoolRequest = carpoolService.createCarpoolRequest(carpoolRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCarpoolRequest);
    }

    @GetMapping
    public ResponseEntity<List<CarpoolRequestDTO>> getAllCarpoolRequests() {
        List<CarpoolRequestDTO> carpoolRequests = carpoolService.getAllCarpoolRequests();
        return ResponseEntity.ok(carpoolRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarpoolRequestDTO> getCarpoolRequestById(@PathVariable UUID id) {
        CarpoolRequestDTO carpoolRequest = carpoolService.getCarpoolRequestById(id);
        return ResponseEntity.ok(carpoolRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteCarpoolRequest(@PathVariable UUID id) {
        carpoolService.deleteCarpoolRequest(id);
        return ResponseEntity.ok(new SuccessResponse());
    }
}
