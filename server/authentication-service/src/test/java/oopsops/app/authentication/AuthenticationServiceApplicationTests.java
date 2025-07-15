package oopsops.app.authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import oopsops.app.authentication.controller.AuthController;
import oopsops.app.authentication.repository.UserRepository;
import oopsops.app.authentication.service.KeycloakService;
import oopsops.app.authentication.service.UserService;

import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest
@ActiveProfiles("test")
class AuthenticationServiceApplicationTests {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void requiredBeansArePresent() {
        // Verify critical beans are properly wired
        assertNotNull(applicationContext.getBean(KeycloakService.class));
        assertNotNull(applicationContext.getBean(AuthController.class));
        assertNotNull(applicationContext.getBean(UserRepository.class));
        assertNotNull(applicationContext.getBean(UserService.class));
    }

}
