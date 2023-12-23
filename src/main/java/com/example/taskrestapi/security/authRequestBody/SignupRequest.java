package com.example.taskrestapi.security.authRequestBody;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    @NotBlank(message = "Username must be specified")
    @Size(max = 40, message = "Username should not be greater than 40")
    private String username;

    @NotBlank(message = "Email must be specified")
    @Size(max = 40, message = "Email should not be greater than 40")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Password must be specified")
    @Size(max = 40, message = "Password should not be greater than 40")
    private String password;
}
