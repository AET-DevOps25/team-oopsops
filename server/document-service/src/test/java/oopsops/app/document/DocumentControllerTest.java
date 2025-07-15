package oopsops.app.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import oopsops.app.document.entity.Document;
import oopsops.app.document.entity.DocumentText;
import oopsops.app.document.service.DocumentService;
import oopsops.app.document.controller.DocumentController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Jwt jwt;

    @MockitoBean
    private DocumentService documentService;

    private Document testDocument;
    private UUID testUserId;
    private DocumentText testDocumentText;

    @BeforeEach
    void setUp() {
        testUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        testDocumentText = new DocumentText();
        testDocumentText.setId(UUID.randomUUID());
        testDocumentText.setText("This is extracted text from PDF");

        testDocument = new Document();
        testDocument.setId(UUID.randomUUID());
        testDocument.setUserId(testUserId);
        testDocument.setFileName("test.pdf");
        testDocument.setFileUrl("file:///tmp/test.pdf");
        testDocument.setStatus("PROCESSED");
        testDocument.setUploadDate(Instant.now());
        testDocument.setDocumentText(testDocumentText);

    }

    @Test
    void getAllDocuments_ShouldReturnListOfDocuments() throws Exception {

        Document doc2 = new Document();
        doc2.setId(UUID.randomUUID());
        doc2.setUserId(testUserId);
        doc2.setFileName("test2.pdf");
        doc2.setFileUrl("http://example.com/test2.pdf");
        doc2.setStatus("PROCESSED");
        doc2.setUploadDate(Instant.now());
        doc2.setDocumentText(testDocumentText);

        List<Document> documents = Arrays.asList(testDocument, doc2);
        when(documentService.getAllByUser(testUserId)).thenReturn(documents);

        mockMvc.perform(get("/api/v1/documents/")
                .with(jwt().jwt(jwt -> jwt.subject(testUserId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(testDocument.getId().toString()))
                .andExpect(jsonPath("$[0].fileName").value("test.pdf"))
                .andExpect(jsonPath("$[0].status").value("PROCESSED"))
                .andExpect(jsonPath("$[1].fileName").value("test2.pdf"));

        verify(documentService).getAllByUser(testUserId);
    }

    @Test
    void getAllDocuments_WithEmptyList_ShouldReturnEmptyArray() throws Exception {
        when(documentService.getAllByUser(testUserId)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/documents/")
                .with(jwt().jwt(jwt -> jwt.subject(testUserId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(documentService).getAllByUser(testUserId);
    }

    @Test
    void getDocumentById_WhenDocumentExists_ShouldReturnDocument() throws Exception {
        when(documentService.getByUserAndId(testUserId, testDocument.getId())).thenReturn(Optional.of(testDocument));

        mockMvc.perform(get("/api/v1/documents/{id}", testDocument.getId())
                .with(jwt().jwt(jwt -> jwt.subject(testUserId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testDocument.getId().toString()))
                .andExpect(jsonPath("$.userId").value(testUserId.toString()))
                .andExpect(jsonPath("$.fileName").value("test.pdf"))
                .andExpect(jsonPath("$.fileUrl").value("file:///tmp/test.pdf"))
                .andExpect(jsonPath("$.status").value("PROCESSED"))
                .andExpect(jsonPath("$.documentText").value("This is extracted text from PDF"));

        verify(documentService).getByUserAndId(testUserId, testDocument.getId());
    }

    @Test
    void getDocumentById_WhenDocumentNotFound_ShouldThrowException() throws Exception {
        when(documentService.getByUserAndId(testUserId, testDocument.getId())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/documents/{id}", testDocument.getId())
                .with(jwt().jwt(jwt -> jwt.subject(testUserId.toString()))))
                .andExpect(status().isInternalServerError());

        verify(documentService).getByUserAndId(testUserId, testDocument.getId());
    }

    @Test
    void upload_WithValidPdfFile_ShouldReturnCreatedDocument() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes());

        when(documentService.uploadAndProcess(eq(testUserId), any(MockMultipartFile.class)))
                .thenReturn(testDocument);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file)
                .with(jwt().jwt(jwt -> jwt.subject(testUserId.toString()))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/documents/" + testDocument.getId()))
                .andExpect(jsonPath("$.id").value(testDocument.getId().toString()))
                .andExpect(jsonPath("$.fileName").value("test.pdf"))
                .andExpect(jsonPath("$.status").value("PROCESSED"));

        verify(documentService).uploadAndProcess(eq(testUserId), any(MockMultipartFile.class));
    }

    @Test
    void upload_WithNoFile_ShouldThrowInvalidFileTypeException() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .with(jwt().jwt(jwt -> jwt.subject(testUserId.toString()))))
                .andExpect(status().isBadRequest());

        verify(documentService, never()).uploadAndProcess(any(), any());
    }

    @Test
    void upload_WithEmptyFile_ShouldThrowInvalidFileTypeException() throws Exception {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[0]);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(emptyFile)
                .with(jwt().jwt(jwt -> jwt.subject(testUserId.toString()))))
                .andExpect(status().isBadRequest());

        verify(documentService, never()).uploadAndProcess(any(), any());
    }

    @Test
    void upload_WithNonPdfFile_ShouldThrowInvalidFileTypeException() throws Exception {
        // Arrange
        MockMultipartFile textFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Text content".getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(textFile)
                .with(jwt().jwt(jwt -> jwt.subject(testUserId.toString()))))
                .andExpect(status().isBadRequest());

        verify(documentService, never()).uploadAndProcess(any(), any());
    }

    @Test
    void upload_WhenServiceThrowsException_ShouldPropagateException() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes());

        when(documentService.uploadAndProcess(eq(testUserId), any(MockMultipartFile.class)))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file)
                .with(jwt().jwt(jwt -> jwt.subject(testUserId.toString()))))
                .andExpect(status().isInternalServerError());

        verify(documentService).uploadAndProcess(eq(testUserId), any(MockMultipartFile.class));
    }

    @Test
    void getAllDocuments_WithInvalidUserId_ShouldHandleException() throws Exception {
        mockMvc.perform(get("/api/v1/documents/")
                .with(jwt().jwt(jwt -> jwt.subject("invalid-uuid"))))
                .andExpect(status().isInternalServerError());

        verify(documentService, never()).getAllByUser(any());
    }

    @Test
    void getDocumentById_WithInvalidUserId_ShouldHandleException() throws Exception {
        mockMvc.perform(get("/api/v1/documents/{id}", testDocument.getId())
                .with(jwt().jwt(jwt -> jwt.subject("invalid-uuid"))))
                .andExpect(status().isInternalServerError());

        verify(documentService, never()).getByUserAndId(any(), any());
    }

    @Test
    void getAllDocuments_WithoutAuthentication_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/documents/"))
                .andExpect(status().isUnauthorized());

        verify(documentService, never()).getAllByUser(any());
    }

    @Test
    void getDocumentById_WithoutAuthentication_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/documents/{id}", testDocument.getId()))
                .andExpect(status().isUnauthorized());

        verify(documentService, never()).getByUserAndId(any(), any());
    }

    @Test
    void upload_WithoutAuthentication_ShouldReturn401() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes());

        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isUnauthorized());

        verify(documentService, never()).uploadAndProcess(any(), any());
    }

}