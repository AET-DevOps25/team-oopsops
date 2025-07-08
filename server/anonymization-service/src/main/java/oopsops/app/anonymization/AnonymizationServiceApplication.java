package oopsops.app.anonymization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration.class
})
public class AnonymizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnonymizationServiceApplication.class, args);
    }
}
