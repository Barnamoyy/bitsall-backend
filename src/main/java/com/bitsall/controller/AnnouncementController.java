package com.bitsall.controller;

import com.bitsall.model.dto.AnnouncementRequest;
import com.bitsall.model.dto.SuccessResponse;
import com.bitsall.service.AnnouncementService;
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
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Validated
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final ObjectMapper objectMapper;
    
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AnnouncementRequest> createAnnouncement(
            @RequestParam(value = "announcement", required = true) String announcementJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        AnnouncementRequest announcementRequest;
        try {
            announcementRequest = objectMapper.readValue(announcementJson, AnnouncementRequest.class);
        } catch (com.fasterxml.jackson.databind.exc.InvalidFormatException e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                if (errorMessage.contains("UUID")) {
                    throw new IllegalArgumentException("Invalid UUID format in request. Please provide a valid UUID (e.g., 'c0b77917-8f23-483b-9e71-9bd03424f996')", e);
                } else if (errorMessage.contains("VisibilityType")) {
                    throw new IllegalArgumentException("Invalid VisibilityType. Valid values are: EVERYONE, SUBSCRIBERS_ONLY, CLUBS, DEPARTMENTS, STUDENT_COUNCIL, GENERAL", e);
                } else if (errorMessage.contains("DepartmentType")) {
                    throw new IllegalArgumentException("Invalid DepartmentType. Valid values are: CSE, ECE, MECHANICAL, CIVIL, ELECTRICAL, CHEMICAL, BIOTECH, GENERAL", e);
                } else if (errorMessage.contains("CarpoolType")) {
                    throw new IllegalArgumentException("Invalid CarpoolType. Valid values are: LOOKING_FOR_PASSENGERS, LOOKING_FOR_RIDE", e);
                } else if (errorMessage.contains("UserRole")) {
                    throw new IllegalArgumentException("Invalid UserRole. Valid values are: STUDENT, DEPARTMENT_HEAD, ADMIN", e);
                } else if (errorMessage.contains("ClubType")) {
                    throw new IllegalArgumentException("Invalid ClubType. Check /api/enums/clubs for valid values", e);
                }
            }
            throw new IllegalArgumentException("Invalid JSON format: " + errorMessage, e);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        }
        
        // Validate the parsed request
        jakarta.validation.Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
        var violations = validator.validate(announcementRequest);
        if (!violations.isEmpty()) {
            throw new jakarta.validation.ConstraintViolationException(violations);
        }
        AnnouncementRequest createdAnnouncement = announcementService.createAnnouncement(announcementRequest, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAnnouncement);
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementRequest>> getAllAnnouncements() {
        List<AnnouncementRequest> announcements = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementRequest> getAnnouncementById(@PathVariable UUID id) {
        AnnouncementRequest announcement = announcementService.getAnnouncementById(id);
        return ResponseEntity.ok(announcement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable UUID id) {
        try {
            announcementService.deleteAnnouncement(id);
            return ResponseEntity.ok(new SuccessResponse());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
