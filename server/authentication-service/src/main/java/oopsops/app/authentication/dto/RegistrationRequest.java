package oopsops.app.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User registration request")
public record RegistrationRequest(
    @Schema(description = "Username", example = "john_doe")
    @NotBlank String username,

    @Schema(description = "Email", example = "john@example.com")
    @Email String email,

    @Schema(description = "Password", example = "securepassword123")
    @NotBlank String password
) {}
