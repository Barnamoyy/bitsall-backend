package com.bitsall.controller;

import com.bitsall.model.entity.Announcement;
import com.bitsall.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_HEAD')")
    public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestPart("announcement") Announcement announcement, @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        Announcement createdAnnouncement = announcementService.createAnnouncement(announcement, image);
        return new ResponseEntity<>(createdAnnouncement, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        return new ResponseEntity<>(announcements, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
