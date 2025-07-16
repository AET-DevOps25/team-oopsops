package oopsops.app.document.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import oopsops.app.document.repository.DocumentRepository;
import oopsops.app.document.entity.Document;
import oopsops.app.document.exception.InvalidFileTypeException;
import oopsops.app.document.exception.PdfParsingException;
import oopsops.app.document.entity.DocumentText;

import java.util.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;
import java.util.List;

@Service
public class DocumentService {

    private final StorageService storageService;
    private final PdfParsingService pdfParsingService;
    private final DocumentRepository documentRepository;

    public DocumentService(
            StorageService storageService,
            PdfParsingService pdfParsingService,
            DocumentRepository documentRepository) {
        this.storageService = storageService;
        this.pdfParsingService = pdfParsingService;
        this.documentRepository = documentRepository;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> getDocumentById(UUID id) {
        return documentRepository.findById(id);
    }

    @Transactional
    public Document uploadAndProcess(UUID userId, MultipartFile file) {

        if (!"application/pdf".equals(file.getContentType())) {
            throw new InvalidFileTypeException("Only PDFs allowed");
        }

        String fileUrl = storageService.store(file);

        Document doc = new Document();
        doc.setUserId(userId);
        doc.setFileName(file.getOriginalFilename());
        doc.setFileUrl(fileUrl);
        doc.setStatus("UPLOADED");
        doc = documentRepository.save(doc);

        String text;
        if (fileUrl != null && fileUrl.startsWith("file:")) {
            Path localPath = Path.of(URI.create(fileUrl));
            text = pdfParsingService.extractText(localPath);
        } else {
            try (InputStream in = file.getInputStream()) {
                text = pdfParsingService.extractText(in);
            } catch (IOException e) {
                throw new PdfParsingException("Failed to read upload stream", e);
            }
        }

        DocumentText dto = new DocumentText();
        dto.setDocument(doc);
        dto.setText(text);
        doc.setDocumentText(dto);
        doc.setStatus("PROCESSED");

        return documentRepository.save(doc);
    }

    public List<Document> getAllByUser(UUID userId) {
        return documentRepository.findAllByUserId(userId);
    }

    public Optional<Document> getByUserAndId(UUID userId, UUID docId) {
        return documentRepository.findByIdAndUserId(docId, userId);
    }

}
