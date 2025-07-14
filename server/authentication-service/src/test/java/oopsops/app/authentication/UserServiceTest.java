package oopsops.app.authentication;

import oopsops.app.authentication.entity.User;
import oopsops.app.authentication.repository.UserRepository;
import oopsops.app.authentication.service.KeycloakService;
import oopsops.app.authentication.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repository;

    @Mock
    KeycloakService keycloakService;
    
    @InjectMocks
    UserService userService;

    private final String USERNAME = "alice";
    private final String EMAIL = "alice@example.com";
    private final String PASSWORD = "s3cr3t";
    private final String ACCESS = "tokenA";
    private final String REFRESH = "tokenR";

    @Test
    void registerUser_conflict() {
        given(repository.findByUsername(USERNAME))
                .willReturn(Optional.of(new User()));

        ResponseStatusException ex = catchThrowableOfType(
                ResponseStatusException.class,
                () -> userService.registerUser(USERNAME, EMAIL, PASSWORD));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        then(keycloakService).shouldHaveNoInteractions();
        then(repository).should(never()).save(any());
    }

    @Test
    void registerUser_happyPath() {
        given(repository.findByUsername(USERNAME)).willReturn(Optional.empty());
        willDoNothing().given(keycloakService)
                .registerUser(USERNAME, EMAIL, PASSWORD);

        userService.registerUser(USERNAME, EMAIL, PASSWORD);

        then(keycloakService).should().registerUser(USERNAME, EMAIL, PASSWORD);
        then(repository).should().save(ArgumentCaptor.forClass(User.class).capture());
    }

    @Test
    void loginNotFound() {
        given(repository.findByUsername(USERNAME)).willReturn(Optional.empty());

        ResponseStatusException ex = catchThrowableOfType(
                ResponseStatusException.class,
                () -> userService.loginWithPassword(USERNAME, PASSWORD));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        then(keycloakService).shouldHaveNoInteractions();
    }

    @Test
    void loginDelegates() {
        User u = new User();
        u.setUsername(USERNAME);
        given(repository.findByUsername(USERNAME)).willReturn(Optional.of(u));
        given(keycloakService.loginWithPassword(USERNAME, PASSWORD))
                .willReturn(Map.of("access_token", ACCESS, "refresh_token", REFRESH));

        Map<String, Object> tokens = userService.loginWithPassword(USERNAME, PASSWORD);
        assertThat(tokens)
                .containsEntry("access_token", ACCESS)
                .containsEntry("refresh_token", REFRESH);
    }

    @Test
    void refreshDelegates() {
        given(keycloakService.refreshWithToken(REFRESH))
                .willReturn(Map.of("access_token", "newA", "refresh_token", "newR"));

        Map<String, Object> out = userService.refreshWithToken(REFRESH);
        assertThat(out).containsKeys("access_token", "refresh_token");
        then(keycloakService).should().refreshWithToken(REFRESH);
    }
}