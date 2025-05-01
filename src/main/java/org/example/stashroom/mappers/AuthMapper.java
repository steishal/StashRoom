package org.example.stashroom.mappers;
import org.example.stashroom.dto.AuthDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "username", source = "dto.username")
    @Mapping(target = "password", source = "dto.password")
    AuthDTO toDto(AuthDTO dto);
}
