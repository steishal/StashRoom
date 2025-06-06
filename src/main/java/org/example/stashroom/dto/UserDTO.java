package org.example.stashroom.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String vkLink;
    private String tgLink;
    private String role;

    public UserDTO(Long id, String username, String email, String phoneNumber,
                   String vkLink, String tgLink, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.vkLink = vkLink;
        this.tgLink = tgLink;
        this.role = role;
    }
}
