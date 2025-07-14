package oopsops.app.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import oopsops.app.document.entity.Document;
import oopsops.app.document.entity.DocumentText;
import oopsops.app.document.repository.DocumentRepository;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DocumentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository repository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static final UUID TEST_USER = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void whenNoDocuments_thenGetAllReturnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/v1/documents/")
                .with(jwt().jwt(jwt -> jwt.subject(TEST_USER.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void givenOneDocument_whenGetAll_thenItShowsUp() throws Exception {

        DocumentText dt = new DocumentText();
        dt.setText("hello world");

        Document doc = new Document();
        doc.setUserId(TEST_USER);
        doc.setFileName("int-test.pdf");
        doc.setFileUrl("file:///tmp/int-test.pdf");
        doc.setStatus("PROCESSED");
        doc.setUploadDate(Instant.now());
        doc.setDocumentText(dt);

        repository.save(doc);

        mockMvc.perform(get("/api/v1/documents/")
                .with(jwt().jwt(jwt -> jwt.subject(TEST_USER.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(doc.getId().toString()))
                .andExpect(jsonPath("$[0].fileName").value("int-test.pdf"))
                .andExpect(jsonPath("$[0].status").value("PROCESSED"));
    }

    @Test
    void givenOneDocument_whenGetById_thenItReturnsIt() throws Exception {
        DocumentText dt = new DocumentText();
        dt.setText("lorem ipsum");

        Document doc = new Document();
        doc.setUserId(TEST_USER);
        doc.setFileName("retrieve.pdf");
        doc.setFileUrl("file:///tmp/retrieve.pdf");
        doc.setStatus("PROCESSED");
        doc.setUploadDate(Instant.now());
        doc.setDocumentText(dt);

        repository.save(doc);

        mockMvc.perform(get("/api/v1/documents/{id}", doc.getId())
                .with(jwt().jwt(jwt -> jwt.subject(TEST_USER.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doc.getId().toString()))
                .andExpect(jsonPath("$.documentText").value("lorem ipsum"));
    }
}
