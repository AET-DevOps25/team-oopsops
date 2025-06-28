package oopsops.app.authentication.service;

import org.springframework.stereotype.Service;

import oopsops.app.authentication.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // public User registerUser(String email, String rawPassword) {
    //     String hashed = encoder.encode(rawPassword);
    //     User user = new User();
    //     user.setEmail(email);
    //     user.setPasswordHash(hashed);
    //     return userRepository.save(user);
    // }

    // public boolean authenticate(String email, String rawPassword) {
    //     return repository.findByEmail(email)
    //         .map(user -> encoder.matches(rawPassword, user.getPasswordHash()))
    //         .orElse(false);
    // }

}
