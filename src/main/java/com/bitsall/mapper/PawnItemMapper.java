package com.bitsall.mapper;

import com.bitsall.model.dto.PawnItemRequest;
import com.bitsall.model.entity.PawnItem;
import com.bitsall.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserService.class})
public abstract class PawnItemMapper {

    @Autowired
    protected UserService userService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sold", ignore = true)
    @Mapping(target = "createdBy", expression = "java(userService.getUserById(pawnItemRequest.getCreatedBy().getId()))")
    public abstract PawnItem toEntity(PawnItemRequest pawnItemRequest);

    public abstract PawnItemRequest toDto(PawnItem pawnItem);
}
