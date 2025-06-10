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

}
