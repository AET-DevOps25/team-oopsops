package oopsops.app.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import oopsops.app.authentication.service.UserService;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("This is a test route.");
    }

    // @PostMapping("/register")
    // public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    //     User user = userService.registerUser(request.getEmail(), request.getPassword());
    //     return ResponseEntity.ok("User created: " + user.getEmail());
    // }

    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    //     boolean success = userService.authenticate(request.getEmail(), request.getPassword());
    //     return success ? ResponseEntity.ok("Authenticated!") : ResponseEntity.status(401).body("Invalid credentials");
    // }
}

