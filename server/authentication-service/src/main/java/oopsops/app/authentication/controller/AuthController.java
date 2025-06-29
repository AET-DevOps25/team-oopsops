package oopsops.app.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import oopsops.app.authentication.dto.RegistrationRequest;
import oopsops.app.authentication.service.UserService;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthController {

    private UserService userService;

    public AuthController(UserService service) {
        this.userService = service;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("This is a test route.");
    }

    @GetMapping("/user")
    public ResponseEntity<?> currentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(jwt.getClaims());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody String body) {
        System.out.println("Incoming: " + body);
        // userService.registerUser(request.username(), request.email(), request.password());
        return ResponseEntity.ok("User registered successfully");
    }

}
