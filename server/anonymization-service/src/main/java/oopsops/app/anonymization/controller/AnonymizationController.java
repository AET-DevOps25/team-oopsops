package oopsops.app.anonymization.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<AnonymizationDto>> getAllAnonymizations() {
        List<AnonymizationDto> anonymizationDtos = anonymizationService.getAllAnonymizations().stream()
            .map(AnonymizationDto::fromDao)
            .toList();
        return ResponseEntity.ok(anonymizationDtos);
    }


        @PostMapping("/{documentId}/add")
        public ResponseEntity<AnonymizationDto> add(@PathVariable UUID documentId,
            @RequestBody AnonymizationRequestBody requestBody) {
            AnonymizationDto dto = new AnonymizationDto(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                documentId,
                requestBody.originalText(),
                requestBody.anonymizedText(),
                requestBody.level(),
                requestBody.changedTerms()
            );
            AnonymizationDto savedDto = anonymizationService.save(dto);
            return ResponseEntity.ok(savedDto);
        }


    @PostMapping("/replace")
    public ResponseEntity<String> anonymizeText(@RequestBody ReplacementRequest request) {
        String anonymizedText = request.getOriginalText();

        for (ChangedTerm pair : request.getChangedTerms()) {
            anonymizedText = anonymizedText.replace(pair.getOriginal(), pair.getAnonymized());
        }

        return ResponseEntity.ok(anonymizedText);
    }
}
