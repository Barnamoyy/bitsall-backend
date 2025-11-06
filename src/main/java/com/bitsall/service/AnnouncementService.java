package com.bitsall.service;

import com.bitsall.mapper.AnnouncementMapper;
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

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final AzureBlobService azureBlobService;
    private final AnnouncementMapper announcementMapper;

    public AnnouncementRequest createAnnouncement(AnnouncementRequest announcementRequest, MultipartFile image) throws IOException {
        // Get the user from the request (authentication not fully configured yet)
        User currentUser = userRepository.findById(announcementRequest.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + announcementRequest.getCreatedBy()));

        // Optional: If authentication is available, verify the user matches
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
                    if (announcementRequest.getDepartment() != authenticatedUser.getDepartment()) {
                        throw new SecurityException("Department heads can only create announcements for their own department.");
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

        Announcement announcement = announcementMapper.toEntity(announcementRequest);

        if (image != null && !image.isEmpty()) {
            String imageUrl = azureBlobService.uploadFile(image, "announcements", announcement.getTitle());
            announcement.setImageUrl(imageUrl);
        }

        Announcement saved = announcementRepository.save(announcement);
        return announcementMapper.toDto(saved);
    }

    public List<AnnouncementRequest> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(announcementMapper::toDto)
                .toList();
    }

    public AnnouncementRequest getAnnouncementById(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        return announcementMapper.toDto(announcement);
    }

    public void deleteAnnouncement(UUID id) {
        if (!announcementRepository.existsById(id)) {
            throw new IllegalArgumentException("Announcement not found with id: " + id);
        }
        announcementRepository.deleteById(id);
    }
}