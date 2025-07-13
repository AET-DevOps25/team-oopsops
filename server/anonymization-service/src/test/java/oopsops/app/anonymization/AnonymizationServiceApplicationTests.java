package oopsops.app.anonymization;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import oopsops.app.anonymization.controller.AnonymizationController;
import oopsops.app.anonymization.repository.AnonymizationRepository;
import oopsops.app.anonymization.service.AnonymizationService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AnonymizationServiceApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

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