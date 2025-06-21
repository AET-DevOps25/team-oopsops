package oopsops.app.document.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import oopsops.app.document.dto.DocumentDto;
/*import oopsops.app.document.models.AnonymizeRequest;
import oopsops.app.document.models.GenAiResponse;*/
import oopsops.app.document.repository.DocumentRepository;
import oopsops.app.document.entity.Document;
import oopsops.app.document.exception.InvalidFileTypeException;
import oopsops.app.document.entity.DocumentText;

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

    @Transactional
    public Document uploadAndProcess(UUID userId, MultipartFile file) {

        // For now we only accept PDF files
        if (!"application/pdf".equals(file.getContentType())) {
            throw new InvalidFileTypeException("Only PDFs allowed");
        }

        String fileUrl = storageService.store(file);

        // Create a new Document entity
        Document doc = new Document();
        doc.setUserId(userId);
        doc.setFileName(file.getOriginalFilename());
        doc.setFileUrl(fileUrl);
        doc.setStatus("UPLOADED");
        doc = documentRepository.save(doc);

        Path localPath = Path.of(URI.create(fileUrl));
        String text = pdfParsingService.extractText(localPath);

        DocumentText documentText = new DocumentText();
        documentText.setDocument(doc);
        documentText.setText(text);
        doc.setDocumentText(documentText);

        doc.setStatus("PROCESSED");

        return documentRepository.save(doc);
    }

    /*public DocumentDto autoAnonymize(UUID documentId, String level) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found"));

        if (document.getDocumentText() == null) {
            throw new RuntimeException("Document has no extracted text.");
        }

        AnonymizeRequest request = new AnonymizeRequest();
        request.setOriginalText(document.getDocumentText().getText());
        request.setLevel(level);

        GenAiResponse response = genAiClient.anonymize(request);

        document.setAnonymizedText(response.getReponseText());
        documentRepository.save(document);

        return new DocumentDto(document);
    }*/

}
