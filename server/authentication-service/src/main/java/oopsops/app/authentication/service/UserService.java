package oopsops.app.authentication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import oopsops.app.authentication.entity.User;
import oopsops.app.authentication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final KeycloakService keycloakService;

    @Transactional
    public void registerUser(String username, String email, String password) {
        if (repository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists. Please log in.");
        }

        keycloakService.registerUser(username, email, password);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail(email);
        repository.save(user);
    }

    @Transactional
    public Map<String, Object> loginWithPassword(String username, String password) {
        if (repository.findByUsername(username).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist. Please register first.");
        }

        return keycloakService.loginWithPassword(username, password);
    }

    public Map<String, Object> refreshWithToken(String refreshToken) {
        return keycloakService.refreshWithToken(refreshToken);
    }

}
