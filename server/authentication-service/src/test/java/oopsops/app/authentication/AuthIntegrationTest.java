package oopsops.app.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import oopsops.app.authentication.dto.RegistrationRequest;
import oopsops.app.authentication.dto.LoginRequest;
import oopsops.app.authentication.repository.UserRepository;
import oopsops.app.authentication.service.KeycloakService;
import oopsops.app.authentication.entity.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private KeycloakService keycloakService;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
        reset(keycloakService);
    }

    @Test
    void register_should_createDbRecord_and_callKeycloak() throws Exception {
        // arrange: make keycloakService.registerUser no-op
        doNothing().when(keycloakService).registerUser("alice", "a@x.com", "pwd");

        var req = new RegistrationRequest("alice", "a@x.com", "pwd");
        mockMvc.perform(post("/api/v1/authentication/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));

        // verify DB state
        assertThat(userRepository.findByUsername("alice")).isPresent();

        // verify we did call out to KeycloakService
        verify(keycloakService).registerUser("alice", "a@x.com", "pwd");
    }

    @Test
    void login_should_returnTokens_fromKeycloakService() throws Exception {

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("bob");
        user.setEmail("bob@gmail.com");

        userRepository.save(user);

        Map<String, Object> tokens = Map.<String, Object>of(
                "access_token", "tok1",
                "refresh_token", "ref1");
        when(keycloakService.loginWithPassword("bob", "secret"))
                .thenReturn(tokens);

        var loginReq = new LoginRequest("bob", "secret");
        mockMvc.perform(post("/api/v1/authentication/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("tok1"))
                .andExpect(jsonPath("$.refresh_token").value("ref1"));

        verify(keycloakService).loginWithPassword("bob", "secret");
    }

    @Test
    void refresh_should_returnNewTokens() throws Exception {

        Map<String, Object> tokens = Map.<String, Object>of("access_token", "newtok", "refresh_token", "newref");

        when(keycloakService.refreshWithToken("oldref")).thenReturn(tokens);

        mockMvc.perform(post("/api/v1/authentication/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(Map.of("refresh_token", "oldref"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("newtok"))
                .andExpect(jsonPath("$.refresh_token").value("newref"));

        verify(keycloakService).refreshWithToken("oldref");
    }

    // helper to JSON-serialize requests
    private static String asJson(Object o) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
