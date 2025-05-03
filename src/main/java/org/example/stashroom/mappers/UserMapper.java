package org.example.stashroom.mappers;
import org.example.stashroom.dto.UserCreateDTO;
import org.example.stashroom.dto.UserDTO;
import org.example.stashroom.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "username", source = "dto.username")
    @Mapping(target = "password", source = "dto.password")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "phoneNumber", source = "dto.phoneNumber")
    @Mapping(target = "vkLink", source = "dto.vkLink")
    @Mapping(target = "tgLink", source = "dto.tgLink")
    User fromCreateDto(UserCreateDTO dto);
}
