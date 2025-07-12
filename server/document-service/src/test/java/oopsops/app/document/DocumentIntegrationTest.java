package oopsops.app.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import oopsops.app.document.entity.Document;
import oopsops.app.document.repository.DocumentRepository;
import oopsops.app.document.service.PdfParsingService;
import oopsops.app.document.service.StorageService;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;

    @MockitoBean
    private StorageService storageService;

    @MockitoBean
    private PdfParsingService pdfParsingService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID testUserId;
    private String testFileUrl;
    private String testExtractedText;

    @BeforeEach
    void setUp() {
        testUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        testFileUrl = "file:///tmp/test.pdf";
        testExtractedText = "This is extracted text from PDF";
        
        // Clean up the repository before each test
        documentRepository.deleteAll();
    }

    @Test
    void fullDocumentUploadFlow_ShouldWorkEndToEnd() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "integration-test.pdf",
            "application/pdf",
            "PDF content for integration test".getBytes()
        );

        when(storageService.store(any())).thenReturn(testFileUrl);
        when(pdfParsingService.extractText(any(Path.class))).thenReturn(testExtractedText);

        // Act - Upload document
        String response = mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("integration-test.pdf"))
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.userId").value(testUserId.toString()))
                .andExpect(jsonPath("$.fileUrl").value(testFileUrl))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract document ID from response
        String documentId = objectMapper.readTree(response).get("id").asText();

        // Assert - Verify document was saved to database
        List<Document> documents = documentRepository.findAll();
        assertEquals(1, documents.size());
        
        Document savedDocument = documents.get(0);
        assertEquals(UUID.fromString(documentId), savedDocument.getId());
        assertEquals(testUserId, savedDocument.getUserId());
        assertEquals("integration-test.pdf", savedDocument.getFileName());
        assertEquals(testFileUrl, savedDocument.getFileUrl());
        assertEquals("PROCESSED", savedDocument.getStatus());
        assertNotNull(savedDocument.getDocumentText());
        assertEquals(testExtractedText, savedDocument.getDocumentText().getText());

        // Act - Retrieve all documents
        mockMvc.perform(get("/api/v1/documents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(documentId))
                .andExpect(jsonPath("$[0].fileName").value("integration-test.pdf"))
                .andExpect(jsonPath("$[0].status").value("PROCESSED"));
    }

    @Test
    void multipleDocumentUpload_ShouldHandleMultipleDocuments() throws Exception {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
            "file",
            "document1.pdf",
            "application/pdf",
            "PDF content 1".getBytes()
        );
        
        MockMultipartFile file2 = new MockMultipartFile(
            "file",
            "document2.pdf",
            "application/pdf",
            "PDF content 2".getBytes()
        );

        when(storageService.store(any()))
            .thenReturn("file:///tmp/document1.pdf")
            .thenReturn("file:///tmp/document2.pdf");
        
        when(pdfParsingService.extractText(any(Path.class)))
            .thenReturn("Text from document 1")
            .thenReturn("Text from document 2");

        // Act - Upload first document
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("document1.pdf"));

        // Act - Upload second document
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file2))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("document2.pdf"));

        // Assert - Verify both documents exist
        List<Document> documents = documentRepository.findAll();
        assertEquals(2, documents.size());

        // Act - Retrieve all documents
        mockMvc.perform(get("/api/v1/documents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void uploadWithInvalidFileType_ShouldNotPersistDocument() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "document.txt",
            "text/plain",
            "This is a text file".getBytes()
        );

        // Act
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isBadRequest());

        // Assert - No document should be persisted
        List<Document> documents = documentRepository.findAll();
        assertEquals(0, documents.size());
    }

    @Test
    void uploadWithStorageFailure_ShouldNotPersistDocument() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "document.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );

        when(storageService.store(any())).thenThrow(new RuntimeException("Storage failure"));

        // Act
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isInternalServerError());

        // Assert - No document should be persisted due to transaction rollback
        List<Document> documents = documentRepository.findAll();
        assertEquals(0, documents.size());
    }

    @Test
    void uploadWithPdfParsingFailure_ShouldNotPersistDocument() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "document.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );

        when(storageService.store(any())).thenReturn(testFileUrl);
        when(pdfParsingService.extractText(any(Path.class)))
            .thenThrow(new RuntimeException("PDF parsing failure"));

        // Act
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isInternalServerError());

        // Assert - No document should be persisted due to transaction rollback
        List<Document> documents = documentRepository.findAll();
        assertEquals(0, documents.size());
    }
}