package org.example.stashroom.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class AvatarUploadDTO {
    private Long userId;
    private String avatar;
}
