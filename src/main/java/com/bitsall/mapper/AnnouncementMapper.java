package com.bitsall.mapper;

import com.bitsall.model.dto.AnnouncementRequest;
import com.bitsall.model.entity.Announcement;
import com.bitsall.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserService.class})
public abstract class AnnouncementMapper {

    @Autowired
    protected UserService userService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "datePosted", ignore = true)
    @Mapping(target = "createdBy", expression = "java(userService.getUserEntityById(announcementRequest.getCreatedBy()))")
    public abstract Announcement toEntity(AnnouncementRequest announcementRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdBy", expression = "java(announcement.getCreatedBy() != null ? announcement.getCreatedBy().getId() : null)")
    public abstract AnnouncementRequest toDto(Announcement announcement);
}

