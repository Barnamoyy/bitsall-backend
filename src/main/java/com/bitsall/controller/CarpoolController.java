package com.bitsall.controller;

import com.bitsall.model.entity.CarpoolRequest;
import com.bitsall.service.CarpoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carpool")
@RequiredArgsConstructor
public class CarpoolController {

    private final CarpoolService carpoolService;

    @PostMapping
    public ResponseEntity<CarpoolRequest> createCarpoolRequest(@Valid @RequestBody CarpoolRequest carpoolRequest) {
        CarpoolRequest createdCarpoolRequest = carpoolService.createCarpoolRequest(carpoolRequest);
        return new ResponseEntity<>(createdCarpoolRequest, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CarpoolRequest>> getAllCarpoolRequests() {
        List<CarpoolRequest> carpoolRequests = carpoolService.getAllCarpoolRequests();
        return new ResponseEntity<>(carpoolRequests, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarpoolRequest(@PathVariable Long id) {
        carpoolService.deleteCarpoolRequest(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
