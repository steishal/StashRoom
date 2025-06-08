package org.example.stashroom.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ForgotPasswordRequest {
    private String email;
    private String phone;
}
