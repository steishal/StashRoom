package org.example.stashroom.mappers;
import org.example.stashroom.dto.AuthDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    AuthDTO toDto(AuthDTO dto);
}
