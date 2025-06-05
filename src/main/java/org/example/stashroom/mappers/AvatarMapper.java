package org.example.stashroom.mappers;

import org.example.stashroom.dto.AvatarUploadDTO;
import org.example.stashroom.entities.Avatar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AvatarMapper {

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "filePath", target = "avatar")
    })
    AvatarUploadDTO toDto(Avatar avatar);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "filePath", source = "avatar"),
            @Mapping(target = "user", ignore = true)
    })
    Avatar toEntity(AvatarUploadDTO dto);
}
