package org.example.stashroom.mappers;
import org.example.stashroom.dto.UserCreateDTO;
import org.example.stashroom.dto.UserDTO;
import org.example.stashroom.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    @Mapping(target = "username", source = "dto.username")
    @Mapping(target = "password", source = "dto.password")
    User fromCreateDto(UserCreateDTO dto);
}
