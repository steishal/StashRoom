package org.example.stashroom.dto;

public record UserDTO(
        Long id,
        String username,
        String email,
        String phoneNumber,
        String vkLink,
        String tgLink
) {}
