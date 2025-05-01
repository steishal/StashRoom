package org.example.stashroom.dto;
// Для отображения публичной информации
public record UserDTO(
        Long id,
        String username,
        String email,
        String phoneNumber,
        String vkLink,
        String tgLink
) {}
