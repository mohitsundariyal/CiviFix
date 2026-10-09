package com.civifix.civifix_backend.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "full name is required")
    private String fullName;
    @NotBlank(message = "email is required")
    @Email(message = "enter a valid email")
    private String email;
    @NotBlank(message = "password is required")
    @Size(min = 6,message = "Password must be at least 6 characters")
    private String password;

}
