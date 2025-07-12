package oopsops.app.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import oopsops.app.document.entity.Document;
import oopsops.app.document.exception.InvalidFileTypeException;
import oopsops.app.document.service.DocumentService;
import oopsops.app.document.controller.DocumentController;

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
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    private Document testDocument;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        testDocument = new Document();
        testDocument.setId(UUID.randomUUID());
        testDocument.setUserId(testUserId);
        testDocument.setFileName("test.pdf");
        testDocument.setFileUrl("file:///tmp/test.pdf");
        testDocument.setStatus("PROCESSED");
        testDocument.setUploadDate(Instant.now());
    }

    @Test
    void getAllDocuments_ShouldReturnDocumentList() throws Exception {
        List<Document> documents = Arrays.asList(testDocument, createSecondDocument());
        when(documentService.getAllDocuments()).thenReturn(documents);

        mockMvc.perform(get("/api/v1/documents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(testDocument.getId().toString()))
                .andExpect(jsonPath("$[0].userId").value(testDocument.getUserId().toString()))
                .andExpect(jsonPath("$[0].fileName").value(testDocument.getFileName()))
                .andExpect(jsonPath("$[0].fileUrl").value(testDocument.getFileUrl()))
                .andExpect(jsonPath("$[0].status").value(testDocument.getStatus()));

        verify(documentService, times(1)).getAllDocuments();
    }

    @Test
    void getAllDocuments_WhenNoDocuments_ShouldReturnEmptyList() throws Exception {
        when(documentService.getAllDocuments()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/documents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(documentService, times(1)).getAllDocuments();
    }

    @Test
    void upload_WithValidPdf_ShouldReturnCreatedDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes());

        when(documentService.uploadAndProcess(eq(testUserId), any())).thenReturn(testDocument);

        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/documents/" + testDocument.getId()))
                .andExpect(jsonPath("$.id").value(testDocument.getId().toString()))
                .andExpect(jsonPath("$.userId").value(testDocument.getUserId().toString()))
                .andExpect(jsonPath("$.fileName").value(testDocument.getFileName()))
                .andExpect(jsonPath("$.fileUrl").value(testDocument.getFileUrl()))
                .andExpect(jsonPath("$.status").value(testDocument.getStatus()));

        verify(documentService, times(1)).uploadAndProcess(eq(testUserId), any());
    }

    @Test
    void upload_WithInvalidFileType_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.doc",
                "application/msword",
                "Word content".getBytes());

        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isBadRequest());

        verify(documentService, never()).uploadAndProcess(any(), any());
    }

    @Test
    void upload_WithNullContentType_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                null,
                "PDF content".getBytes());

        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isBadRequest());

        verify(documentService, never()).uploadAndProcess(any(), any());
    }

    @Test
    void upload_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[0]);

        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isBadRequest());

        verify(documentService, never()).uploadAndProcess(any(), any());
    }

    @Test
    void upload_WithNoFile() throws Exception {
        mockMvc.perform(multipart("/api/v1/documents/upload"))
                .andExpect(status().isBadRequest());

        verify(documentService, never()).uploadAndProcess(any(), any());
    }

    @Test
    void upload_WhenServiceThrowsInvalidFileTypeException_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes());

        when(documentService.uploadAndProcess(eq(testUserId), any()))
                .thenThrow(new InvalidFileTypeException("Only PDFs allowed"));

        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isBadRequest());

        verify(documentService, times(1)).uploadAndProcess(eq(testUserId), any());
    }

    @Test
    void upload_WhenServiceThrowsRuntimeException_ShouldReturnInternalServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes());

        when(documentService.uploadAndProcess(eq(testUserId), any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(file))
                .andExpect(status().isInternalServerError());

        verify(documentService, times(1)).uploadAndProcess(eq(testUserId), any());
    }

    private Document createSecondDocument() {
        Document doc = new Document();
        doc.setId(UUID.randomUUID());
        doc.setUserId(testUserId);
        doc.setFileName("second.pdf");
        doc.setFileUrl("file:///tmp/second.pdf");
        doc.setStatus("PROCESSED");
        doc.setUploadDate(Instant.now());
        return doc;
    }
}