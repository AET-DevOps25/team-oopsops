package oopsops.app.anonymization;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import oopsops.app.anonymization.controller.AnonymizationController;
import oopsops.app.anonymization.repository.AnonymizationRepository;
import oopsops.app.anonymization.service.AnonymizationService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AnonymizationServiceApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void requiredBeansArePresent() {
        // Verify critical beans are properly wired
        assertNotNull(applicationContext.getBean(AnonymizationService.class));
        assertNotNull(applicationContext.getBean(AnonymizationController.class));
        assertNotNull(applicationContext.getBean(AnonymizationRepository.class));
    }
}