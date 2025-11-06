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

    @Mapping(target = "id", ignore = false)
    @Mapping(target = "createdBy", expression = "java(userService.getUserEntityById(carpoolRequestDTO.getCreatedBy()))")
    public abstract CarpoolRequest toEntity(CarpoolRequestDTO carpoolRequestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdBy", expression = "java(carpoolRequest.getCreatedBy() != null ? carpoolRequest.getCreatedBy().getId() : null)")
    public abstract CarpoolRequestDTO toDto(CarpoolRequest carpoolRequest);
}
