package oopsops.app.document;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class DocumentServiceApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
        .withDatabaseName("documentdb")
        .withUsername("dev_user")
        .withPassword("dev_pass");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        // Point Spring’s DataSource at the Testcontainer’s JDBC URL
        registry.add("spring.datasource.url",    postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void contextLoads() {
        // If the app context starts without exception, the test passes.
    }
}
