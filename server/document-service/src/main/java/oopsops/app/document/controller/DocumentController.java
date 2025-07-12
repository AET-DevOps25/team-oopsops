package oopsops.app.document.controller;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import oopsops.app.document.dto.DocumentDto;
import oopsops.app.document.entity.Document;
import oopsops.app.document.service.DocumentService;
import oopsops.app.document.exception.InvalidFileTypeException;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAllDocuments(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<Document> documents = documentService.getAllByUser(userId);
        List<DocumentDto> dtos = documents.stream()
                .map(document -> new DocumentDto(
                        document.getId(),
                        document.getUserId(),
                        document.getFileName(),
                        document.getFileUrl(),
                        document.getStatus(),
                        document.getUploadDate(),
                        document.getDocumentText().getText()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID docId) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Document document = documentService.getByUserAndId(userId, docId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        DocumentDto dto = new DocumentDto(
                document.getId(),
                document.getUserId(),
                document.getFileName(),
                document.getFileUrl(),
                document.getStatus(),
                document.getUploadDate(),
                document.getDocumentText().getText());

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("No file uploaded");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new InvalidFileTypeException("Only PDFs allowed");
        }

        UUID userId = UUID.fromString(jwt.getSubject());
        Document document = documentService.uploadAndProcess(userId, file);

        var dto = new DocumentDto(
                document.getId(),
                document.getUserId(),
                document.getFileName(),
                document.getFileUrl(),
                document.getStatus(),
                document.getUploadDate(),
                document.getDocumentText().getText());

        return ResponseEntity
                .created(URI.create("/api/v1/documents/" + document.getId()))
                .body(dto);
    }

}
