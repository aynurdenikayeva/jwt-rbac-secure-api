package com.aynur.payment.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    @Email @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;
}