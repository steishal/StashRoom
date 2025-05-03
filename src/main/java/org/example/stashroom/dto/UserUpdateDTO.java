package org.example.stashroom.dto;
import jakarta.validation.constraints.Email;

public record UserUpdateDTO(
        @Email String email,
        String phoneNumber,
        String vkLink,
        String tgLink
) {}
