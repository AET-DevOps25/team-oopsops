package oopsops.app.authentication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import oopsops.app.authentication.entity.User;
import oopsops.app.authentication.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final KeycloakService keycloakService;

    @Transactional
    public void registerUser(String username, String email, String password) {

        keycloakService.registerUser(username, email, password);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail(email);
        repository.save(user);
    }
}
