package com.bitsall.service;

import com.bitsall.model.entity.Announcement;
import com.bitsall.model.entity.User;
import com.bitsall.model.enums.UserRole;
import com.bitsall.repository.AnnouncementRepository;
import com.bitsall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public Announcement createAnnouncement(Announcement announcement) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        if (currentUser.getRole() == UserRole.DEPARTMENT_HEAD) {
            if (announcement.getDepartment() != currentUser.getDepartment()) {
                throw new SecurityException("Department heads can only create announcements for their own department.");
            }
        }

        announcement.setCreatedBy(currentUser);
        return announcementRepository.save(announcement);
    }

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}