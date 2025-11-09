package com.bitsall.service;

import com.bitsall.model.dto.AnnouncementRequest;
import com.bitsall.model.entity.Announcement;
import com.bitsall.model.entity.User;
import com.bitsall.model.enums.UserRole;
import com.bitsall.repository.AnnouncementRepository;
import com.bitsall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final AzureBlobService azureBlobService;

    public AnnouncementRequest createAnnouncement(AnnouncementRequest announcementRequest, MultipartFile image) throws IOException {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
                String userEmail = authentication.getName();
                User authenticatedUser = userRepository.findByEmail(userEmail)
                        .orElse(null);
                if (authenticatedUser != null && !authenticatedUser.getId().equals(announcementRequest.getCreatedBy())) {
                    throw new SecurityException("User ID in request does not match authenticated user");
                }
                
                if (authenticatedUser != null && authenticatedUser.getRole() == UserRole.DEPARTMENT_HEAD) {
                    if (authenticatedUser.getDepartments() == null || 
                        !authenticatedUser.getDepartments().contains(announcementRequest.getDepartment())) {
                        throw new SecurityException("Department heads can only create announcements for their assigned departments.");
                    }
                }
            }
        } catch (SecurityException e) {
            // Re-throw security exceptions
            throw e;
        } catch (Exception e) {
            // If authentication is not available, skip the check (for development)
            // In production, you should have proper authentication
        }

        User user = userRepository.findById(announcementRequest.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + announcementRequest.getCreatedBy()));

        Announcement announcement = Announcement.builder()
                .title(announcementRequest.getTitle())
                .body(announcementRequest.getBody())
                .expiryDate(announcementRequest.getExpiryDate())
                .links(announcementRequest.getLinks())
                .visibility(announcementRequest.getVisibility())
                .department(announcementRequest.getDepartment())
                .createdBy(user)
                .build();

        if (image != null && !image.isEmpty()) {
            String imageUrl = azureBlobService.uploadFile(image, "announcements", announcement.getTitle());
            announcement.setImageUrl(imageUrl);
        }

        Announcement saved = announcementRepository.save(announcement);
        return toDto(saved);
    }

    public List<AnnouncementRequest> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AnnouncementRequest getAnnouncementById(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        return toDto(announcement);
    }

    private AnnouncementRequest toDto(Announcement announcement) {
        AnnouncementRequest dto = new AnnouncementRequest();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setBody(announcement.getBody());
        dto.setDatePosted(announcement.getDatePosted());
        dto.setExpiryDate(announcement.getExpiryDate());
        dto.setLinks(announcement.getLinks());
        dto.setVisibility(announcement.getVisibility());
        dto.setDepartment(announcement.getDepartment());
        dto.setImageUrl(announcement.getImageUrl());
        dto.setCreatedBy(announcement.getCreatedBy() != null ? announcement.getCreatedBy().getId() : null);
        return dto;
    }

    public void deleteAnnouncement(UUID id) {
        if (!announcementRepository.existsById(id)) {
            throw new IllegalArgumentException("Announcement not found with id: " + id);
        }
        announcementRepository.deleteById(id);
    }
}