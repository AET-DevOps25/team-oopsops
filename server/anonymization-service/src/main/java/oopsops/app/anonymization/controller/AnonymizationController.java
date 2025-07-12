package oopsops.app.anonymization.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import oopsops.app.anonymization.dto.AnonymizationDto;
import oopsops.app.anonymization.models.AnonymizationRequestBody;
import oopsops.app.anonymization.models.ChangedTerm;
import oopsops.app.anonymization.models.ReplacementRequest;
import oopsops.app.anonymization.service.AnonymizationService;

@RestController
@RequestMapping("/api/v1/anonymization")
public class AnonymizationController {

    public AnonymizationService anonymizationService;

    public AnonymizationController(AnonymizationService anonymizationService) {
        this.anonymizationService = anonymizationService;
    }

    @GetMapping
    public ResponseEntity<List<AnonymizationDto>> getAllAnonymizations(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<AnonymizationDto> anonymizationDtos = anonymizationService.getAllAnonymizations(userId).stream()
                .map(AnonymizationDto::fromDao)
                .toList();
        return ResponseEntity.ok(anonymizationDtos);
    }

    @PostMapping("/{documentId}/add")
    public ResponseEntity<AnonymizationDto> add(
            @PathVariable UUID documentId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AnonymizationRequestBody requestBody) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Optional<AnonymizationDto> existing = anonymizationService.findByDocumentId(documentId);

        AnonymizationDto dto;
        if (existing.isPresent()) {
            dto = new AnonymizationDto(
                    existing.get().id(),
                    OffsetDateTime.now(),
                    documentId,
                    userId,
                    requestBody.originalText(),
                    requestBody.anonymizedText(),
                    requestBody.level(),
                    requestBody.changedTerms());
        } else {
            dto = new AnonymizationDto(
                    null,
                    OffsetDateTime.now(),
                    documentId,
                    userId,
                    requestBody.originalText(),
                    requestBody.anonymizedText(),
                    requestBody.level(),
                    requestBody.changedTerms());
        }

        AnonymizationDto savedDto = anonymizationService.save(dto);
        return ResponseEntity.ok(savedDto);
    }

    @PostMapping("/replace")
    public ResponseEntity<String> anonymizeText(@RequestBody ReplacementRequest request) {
        String anonymizedText = request.getOriginalText();

        String newAnonymizedText = anonymizedText.replace("\n", " ").replace("\r", " ");

        newAnonymizedText = newAnonymizedText.replaceAll(" +", " ");

        List<ChangedTerm> sortedTerms = new ArrayList<>(request.getChangedTerms());
        sortedTerms.sort((a, b) -> Integer.compare(b.getOriginal().length(), a.getOriginal().length()));

        for (ChangedTerm term : sortedTerms) {
            newAnonymizedText = newAnonymizedText.replace(term.getOriginal(), term.getAnonymized());
        }

        return ResponseEntity.ok(newAnonymizedText);
    }

    @GetMapping("/{id}/download")
    public void downloadAnonymizedPdf(@PathVariable UUID id, HttpServletResponse response) {
        AnonymizationDto dto = anonymizationService.getById(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=anonymized_document.pdf");

        try {
            OutputStream out = response.getOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph(dto.anonymizedText()));
            document.close();
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }
    }
}
