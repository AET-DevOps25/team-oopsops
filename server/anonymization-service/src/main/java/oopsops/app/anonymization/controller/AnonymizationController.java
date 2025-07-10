package oopsops.app.anonymization.controller;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.Normalizer;

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
                requestBody.changedTerms());
        AnonymizationDto savedDto = anonymizationService.save(dto);
        return ResponseEntity.ok(savedDto);
    }

    private String normalize(String input) {
    if (input == null) return "";
    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
    return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().trim();
}

    private String flexibleReplace(String text, String original, String replacement) {
    Pattern flexiblePattern = Pattern.compile(
        Pattern.quote(original), 
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );
    Matcher matcher = flexiblePattern.matcher(text);
    return matcher.replaceAll(replacement);
}

    @PostMapping("/replace")
    public ResponseEntity<String> anonymizeText(@RequestBody ReplacementRequest request) {
        String anonymizedText = request.getOriginalText();

    System.out.println("Original text:");
    System.out.println(anonymizedText);

    // Sort longest first to avoid partial replacements
    List<ChangedTerm> sortedTerms = new ArrayList<>(request.getChangedTerms());
    sortedTerms.sort((a, b) -> Integer.compare(b.getOriginal().length(), a.getOriginal().length()));

    for (ChangedTerm term : sortedTerms) {
        String original = term.getOriginal();
        String replacement = term.getAnonymized();

        System.out.println("Trying to replace (flexible match): '" + original + "'");

        // Build regex pattern to match normalized, case-insensitive version
        String normalizedOriginal = normalize(original);
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(normalizedOriginal) + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Matcher matcher = pattern.matcher(normalize(anonymizedText));
        if (matcher.find()) {
            // Replace the actual (non-normalized) original string in the real text
            anonymizedText = flexibleReplace(anonymizedText, original, replacement);
            System.out.println("Replaced successfully");
        } else {
            System.out.println("NOT FOUND in text: '" + original + "'");
        }
    }

        return ResponseEntity.ok(anonymizedText);
    }
}
