package oopsops.app.anonymization.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import oopsops.app.anonymization.entity.AnonymizationEntity;
import oopsops.app.anonymization.models.ChangedTerm;

public record AnonymizationDto(
    UUID id,
    OffsetDateTime created,
    UUID documentId,
    String originalText,
    String anonymizedText,
    String anonymization_level,
    List<ChangedTerm> changedTerms) {
    public static AnonymizationDto fromDao(AnonymizationEntity entity) {
        return new AnonymizationDto(
            entity.getDocumentId(),
            entity.getCreated(),
            entity.getDocumentId(),
            entity.getOriginalText(),
            entity.getAnonymizedText(),
            entity.getAnonymization_level(),
            entity.getChangedTerms()
        );
    }

    public AnonymizationEntity toDao() {
        AnonymizationEntity entity = new AnonymizationEntity();
        if (created != null) {
            entity.setCreated(created);
        }
        entity.setDocumentId(documentId);
        entity.setOriginalText(originalText);
        entity.setAnonymizedText(anonymizedText);
        entity.setAnonymization_level(anonymization_level);
        entity.setChangedTerms(changedTerms);
        return entity;
    }
}



