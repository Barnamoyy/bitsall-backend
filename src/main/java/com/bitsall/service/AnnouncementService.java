package com.bitsall.service;

import com.bitsall.model.dto.AnnouncementRequest;
import com.bitsall.model.entity.Announcement;
import com.bitsall.model.entity.User;
import com.bitsall.model.enums.ClubType;
import com.bitsall.model.enums.UserRole;
import com.bitsall.model.enums.VisibilityType;
import com.bitsall.repository.AnnouncementRepository;
import com.bitsall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final AzureBlobService azureBlobService;

    public AnnouncementRequest createAnnouncement(AnnouncementRequest announcementRequest, MultipartFile image) throws IOException {
        User user = userRepository.findById(announcementRequest.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + announcementRequest.getCreatedBy()));

        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
                String userEmail = authentication.getName();
                User authenticatedUser = userRepository.findByEmail(userEmail)
                        .orElse(null);
                if (authenticatedUser != null && !authenticatedUser.getId().equals(announcementRequest.getCreatedBy())) {
                    throw new SecurityException("User ID in request does not match authenticated user");
                }
                
                // Verify user has permission to create announcements
                if (!isAuthorizedRole(user.getRole())) {
                    throw new SecurityException("You don't have permission to create announcements");
                }
                
                // Validate visibility settings match user's role
                if (announcementRequest.getVisibility() == VisibilityType.CLUBS) {
                    if (user.getRole() != UserRole.CLUB_HEAD && user.getRole() != UserRole.ADMIN) {
                        throw new SecurityException("Only Club Heads and Admins can create club-specific announcements");
                    }
                    
                    // For Club Heads, ensure they can only post to their own club
                    if (user.getRole() == UserRole.CLUB_HEAD) {
                        if (user.getClubs() == null || user.getClubs().isEmpty()) {
                            throw new SecurityException("Club Head must be assigned to a club");
                        }
                        ClubType userClub = user.getClubs().get(0);
                        announcementRequest.setClub(userClub); // Force to their club
                    }
                }
                
                // Validate department visibility
                if (announcementRequest.getVisibility() == VisibilityType.DEPARTMENTS) {
                    if (user.getRole() == UserRole.DEPARTMENT_HEAD) {
                        if (user.getDepartments() == null || 
                            !user.getDepartments().contains(announcementRequest.getDepartment())) {
                            throw new SecurityException("Department heads can only create announcements for their assigned departments.");
                        }
                    }
                }
                
                // Validate student council visibility
                if (announcementRequest.getVisibility() == VisibilityType.STUDENT_COUNCIL) {
                    if (user.getRole() != UserRole.STUDENT_COUNCIL && user.getRole() != UserRole.ADMIN) {
                        throw new SecurityException("Only Student Council members can create student council announcements");
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

        Announcement announcement = Announcement.builder()
                .title(announcementRequest.getTitle())
                .body(announcementRequest.getBody())
                .expiryDate(announcementRequest.getExpiryDate())
                .links(announcementRequest.getLinks())
                .visibility(announcementRequest.getVisibility())
                .department(announcementRequest.getDepartment())
                .club(announcementRequest.getClub())
                .createdBy(user)
                .build();

        if (image != null && !image.isEmpty()) {
            String imageUrl = azureBlobService.uploadFile(image, "announcements", announcement.getTitle());
            announcement.setImageUrl(imageUrl);
        }

        Announcement saved = announcementRepository.save(announcement);
        
        // Audit logging
        log.info("Announcement created: id={}, createdBy={}, role={}, visibility={}, club={}, department={}", 
            saved.getId(),
            user.getId(),
            user.getRole(),
            saved.getVisibility(),
            saved.getClub(),
            saved.getDepartment()
        );
        
        return toDto(saved);
    }

    /**
     * Get all announcements visible to the current authenticated user
     */
    public List<AnnouncementRequest> getAllAnnouncements() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
                String userEmail = authentication.getName();
                User currentUser = userRepository.findByEmail(userEmail).orElse(null);
                if (currentUser != null) {
                    return getVisibleAnnouncementsForUser(currentUser);
                }
            }
        } catch (Exception e) {
            log.warn("Error getting user context, returning all announcements: {}", e.getMessage());
        }
        
        // Fallback to all announcements if no authentication
        return announcementRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get announcements visible to a specific user based on their context
     */
    public List<AnnouncementRequest> getVisibleAnnouncementsForUser(User user) {
        List<ClubType> userClubs = user.getClubs() != null ? user.getClubs() : new ArrayList<>();
        List<com.bitsall.model.enums.DepartmentType> userDepartments = user.getDepartments() != null ? user.getDepartments() : new ArrayList<>();
        boolean isStudentCouncil = user.getRole() == UserRole.STUDENT_COUNCIL;
        
        // Handle empty lists for JPA query
        if (userClubs.isEmpty()) {
            userClubs = new ArrayList<>();
            userClubs.add(ClubType.NONE); // Add a placeholder that won't match
        }
        if (userDepartments.isEmpty()) {
            userDepartments = new ArrayList<>();
        }
        
        List<Announcement> announcements = announcementRepository.findVisibleAnnouncementsForUser(
            userClubs, userDepartments, isStudentCouncil
        );
        
        return announcements.stream()
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
        dto.setClub(announcement.getClub());
        dto.setImageUrl(announcement.getImageUrl());
        dto.setCreatedBy(announcement.getCreatedBy() != null ? announcement.getCreatedBy().getId() : null);
        return dto;
    }

    public void deleteAnnouncement(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
                String userEmail = authentication.getName();
                User currentUser = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new SecurityException("User not found"));
                
                // Check if user created the announcement
                if (!announcement.getCreatedBy().getId().equals(currentUser.getId())) {
                    throw new SecurityException("You can only delete your own announcements");
                }
                
                // Check if user still has authorization to manage announcements
                if (!isAuthorizedRole(currentUser.getRole())) {
                    throw new SecurityException("You no longer have permission to manage announcements");
                }
                
                log.info("Announcement deleted: id={}, deletedBy={}, role={}", 
                    id, currentUser.getId(), currentUser.getRole());
            }
        } catch (SecurityException e) {
            // Re-throw security exceptions
            throw e;
        } catch (Exception e) {
            // If authentication is not available, allow deletion (for development)
            log.warn("Deleting announcement without authentication check: {}", e.getMessage());
        }
        
        announcementRepository.deleteById(id);
    }
    
    /**
     * Check if a user role is authorized to create/manage announcements
     */
    private boolean isAuthorizedRole(UserRole role) {
        return role == UserRole.FACULTY
            || role == UserRole.CLUB_HEAD
            || role == UserRole.STUDENT_COUNCIL
            || role == UserRole.DEPARTMENT_HEAD
            || role == UserRole.ADMIN;
    }
    
    /**
     * Check if an announcement is visible to a specific user
     */
    private boolean isVisibleToUser(Announcement announcement, User user) {
        VisibilityType visibility = announcement.getVisibility();
        
        // EVERYONE - visible to all authenticated users
        if (visibility == VisibilityType.EVERYONE || visibility == null) {
            return true;
        }
        
        // CLUBS - visible only to members of the specified club
        if (visibility == VisibilityType.CLUBS) {
            ClubType announcementClub = announcement.getClub();
            if (announcementClub == null) return true; // No club specified
            
            List<ClubType> userClubs = user.getClubs();
            return userClubs != null && userClubs.contains(announcementClub);
        }
        
        // DEPARTMENTS - visible only to members of the specified department
        if (visibility == VisibilityType.DEPARTMENTS) {
            com.bitsall.model.enums.DepartmentType announcementDept = announcement.getDepartment();
            if (announcementDept == null) return true; // No department specified
            
            List<com.bitsall.model.enums.DepartmentType> userDepts = user.getDepartments();
            return userDepts != null && userDepts.contains(announcementDept);
        }
        
        // STUDENT_COUNCIL - visible only to student council members
        if (visibility == VisibilityType.STUDENT_COUNCIL) {
            return user.getRole() == UserRole.STUDENT_COUNCIL;
        }
        
        // SUBSCRIBERS_ONLY - implement based on your subscription logic
        if (visibility == VisibilityType.SUBSCRIBERS_ONLY) {
            // TODO: Implement subscription checking
            return false;
        }
        
        // Default: hide if not matching any condition
        return false;
    }
}