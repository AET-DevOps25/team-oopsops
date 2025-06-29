package oopsops.app.authentication.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.transaction.Transactional;
import oopsops.app.authentication.entity.User;
import oopsops.app.authentication.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void registerUser(String username, String email, String password) {

        String userId = registerUserInKeycloak(username, email, password);

        User profile = new User();
        profile.setId(UUID.fromString(userId));
        profile.setEmail(email);
        profile.setUsername(username);
        repository.save(profile);
    }

    /**
     * Registers user in Keycloak and returns the new user id
     */
    public String registerUserInKeycloak(String username, String email, String password) {
        RestTemplate restTemplate = new RestTemplate();

        String adminToken = getAdminToken();

        // Map<String, Object> userPayload = Map.of(
        // "username", username,
        // "email", email,
        // "enabled", true,
        // "credentials", List.of(
        // Map.of(
        // "type", "password",
        // "value", password,
        // "temporary", false)));

        if (username == null || email == null || password == null) {
            throw new IllegalArgumentException("Username, email, and password must not be null");
        }

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", username);
        userPayload.put("email", email);
        userPayload.put("enabled", true);
        userPayload.put("credentials", List.of(credentials));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        System.out.println("Registering user with email=" + email + " username=" + username);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userPayload, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://keycloak:8080/admin/realms/oopsops/users",
                entity,
                Void.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String location = response.getHeaders().getLocation().toString();
            String userId = location.substring(location.lastIndexOf("/") + 1);
            return userId;
        } else {
            throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusCode());
        }
    }

    protected String getAdminToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", "admin");
        form.add("password", "admin");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        Map body = restTemplate.postForObject(
                "http://keycloak:8080/realms/master/protocol/openid-connect/token",
                entity,
                Map.class);
        return (String) body.get("access_token");
    }

}
