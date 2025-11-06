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
    @Mapping(target = "createdBy", expression = "java(userService.getUserEntityById(pawnItemRequest.getCreatedBy()))")
    public abstract PawnItem toEntity(PawnItemRequest pawnItemRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdBy", expression = "java(pawnItem.getCreatedBy() != null ? pawnItem.getCreatedBy().getId() : null)")
    public abstract PawnItemRequest toDto(PawnItem pawnItem);
}
