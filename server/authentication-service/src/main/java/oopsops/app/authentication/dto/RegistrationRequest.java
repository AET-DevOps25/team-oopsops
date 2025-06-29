package oopsops.app.authentication.dto;

public record RegistrationRequest(
    String username, 
    String email, 
    String password
) {}