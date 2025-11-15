package com.bitsall.repository;

import com.bitsall.model.entity.Announcement;
import com.bitsall.model.enums.ClubType;
import com.bitsall.model.enums.DepartmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    
    /**
     * Find all announcements visible to everyone
     */
    @Query("SELECT a FROM Announcement a WHERE a.visibility = 'EVERYONE' OR a.visibility IS NULL ORDER BY a.datePosted DESC")
    List<Announcement> findPublicAnnouncements();
    
    /**
     * Find announcements visible to a user based on their clubs, departments, and student council status
     */
    @Query("SELECT DISTINCT a FROM Announcement a WHERE " +
           "(a.visibility = 'EVERYONE' OR a.visibility IS NULL) " +
           "OR (a.visibility = 'CLUBS' AND a.club IN :userClubs) " +
           "OR (a.visibility = 'DEPARTMENTS' AND a.department IN :userDepartments) " +
           "OR (a.visibility = 'STUDENT_COUNCIL' AND :isStudentCouncil = true) " +
           "ORDER BY a.datePosted DESC")
    List<Announcement> findVisibleAnnouncementsForUser(
        @Param("userClubs") List<ClubType> userClubs,
        @Param("userDepartments") List<DepartmentType> userDepartments,
        @Param("isStudentCouncil") boolean isStudentCouncil
    );
}
