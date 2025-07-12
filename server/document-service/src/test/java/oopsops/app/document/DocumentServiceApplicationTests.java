package oopsops.app.document;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import oopsops.app.document.service.DocumentService;
import oopsops.app.document.controller.DocumentController;
import oopsops.app.document.repository.DocumentRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DocumentServiceApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void requiredBeansArePresent() {
        // Verify critical beans are properly wired
        assertNotNull(applicationContext.getBean(DocumentService.class));
        assertNotNull(applicationContext.getBean(DocumentController.class));
        assertNotNull(applicationContext.getBean(DocumentRepository.class));
    }
}