package org.example.stashroom.mappers;
import org.example.stashroom.dto.FollowerDTO;
import org.example.stashroom.entities.Follower;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowerMapper {
    @Mapping(target = "followerUsername", source = "follower.username")
    @Mapping(target = "followingUsername", source = "following.username")
    FollowerDTO toDto(Follower follower);
}
