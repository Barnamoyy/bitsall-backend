package com.bitsall.mapper;

import com.bitsall.model.dto.UserRequest;
import com.bitsall.model.dto.UserResponse;
import com.bitsall.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateJoined", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "announcements", ignore = true)
    @Mapping(target = "carpoolRequests", ignore = true)
    @Mapping(target = "pawnItems", ignore = true)
    User toEntity(UserRequest userRequest);

    UserResponse toResponse(User user);

    UserRequest toDto(User user);
}

