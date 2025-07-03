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

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        userService.registerUser(request.username(), request.email(), request.password());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.loginWithPassword(request.username(), request.password());
        return ResponseEntity.ok(Map.of("access_token", token));
    }

}
