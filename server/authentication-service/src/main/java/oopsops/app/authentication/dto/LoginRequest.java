package oopsops.app.authentication.dto;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User login request")
public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}
