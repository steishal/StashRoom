package org.example.stashroom.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateDTO(
        @NotBlank String username,
        @NotBlank String password,
        @Email String email,
        String phoneNumber,
        String vkLink,
        String tgLink
) {}
