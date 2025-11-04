package com.bitsall.mapper;

import com.bitsall.model.dto.CarpoolRequestDTO;
import com.bitsall.model.entity.CarpoolRequest;
import com.bitsall.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserService.class})
public abstract class CarpoolRequestMapper {

    @Autowired
    protected UserService userService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", expression = "java(userService.getUserById(carpoolRequestDTO.getCreatedBy().getId()))")
    public abstract CarpoolRequest toEntity(CarpoolRequestDTO carpoolRequestDTO);

    public abstract CarpoolRequestDTO toDto(CarpoolRequest carpoolRequest);
}
