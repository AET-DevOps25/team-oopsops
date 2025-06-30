package oopsops.app.document.controller;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<List<DocumentDto>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        List<DocumentDto> dtos = documents.stream()
            .map(document -> new DocumentDto(
                document.getId(),
                document.getUserId(),
                document.getFileName(),
                document.getFileUrl(),
                document.getStatus(),
                document.getUploadDate()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> upload(@RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("No file uploaded");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new InvalidFileTypeException("Only PDFs allowed");
        }

        // Temporary user ID for testing purposes
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Document document = documentService.uploadAndProcess(userId, file);

        var dto = new DocumentDto(
            document.getId(),
            document.getUserId(),
            document.getFileName(),
            document.getFileUrl(),
            document.getStatus(),
            document.getUploadDate()
        );

        return ResponseEntity
            .created(URI.create("/api/v1/documents/" + document.getId()))
            .body(dto);
    }


}
