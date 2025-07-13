package oopsops.app.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

import oopsops.app.authentication.dto.RegistrationRequest;
import oopsops.app.authentication.dto.LoginRequest;
import oopsops.app.authentication.service.UserService;

import lombok.RequiredArgsConstructor;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Authentication", description = "Handles user registration and login")
@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Registers a new user and creates their Keycloak and DB records")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        userService.registerUser(request.username(), request.email(), request.password());
        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(summary = "Login user", description = "Logs in user and returns access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> tokens = userService.loginWithPassword(request.username(), request.password());
        return ResponseEntity.ok(tokens);
    }

    @Operation(summary = "Refresh access token", description = "Refreshes the access token using the refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required"));
        }
        Map<String, Object> tokens = userService.refreshWithToken(refreshToken);
        return ResponseEntity.ok(tokens);
    }

}
