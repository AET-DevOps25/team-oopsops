package oopsops.app.anonymization.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import oopsops.app.anonymization.dao.AnonymizationDao;
import oopsops.app.anonymization.models.ChangedTerm;

public class AnonymizationDto {

    private final UUID id;

    private final OffsetDateTime created;

    private final UUID documentId;

    private final String originalText;

    private final String anonymizedText;

    private final String anonymization_level;

    private final List<ChangedTerm> changedTerms;

    public AnonymizationDto(final UUID id, final OffsetDateTime created, final UUID documentId, final String originalText, final String anonymizedText, final String anonymization_level, final List<ChangedTerm> changedTerms) {
        this.id = id;
        this.created = created;
        this.documentId = documentId;
        this.originalText = originalText;
        this.anonymizedText = anonymizedText;
        this.anonymization_level = anonymization_level;
        this.changedTerms = changedTerms;
    }

    public UUID getId() {
        return id;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getAnonymizedText() {
        return anonymizedText;
    }

    public String getAnonymization_level() {
        return anonymization_level;
    }

    public List<ChangedTerm> getChangedTerms() {
        return changedTerms;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public AnonymizationDao toDao() {
        return new AnonymizationDao(
            id,
            created,
            documentId,
            originalText,
            anonymizedText,
            anonymization_level,
            changedTerms);
    }

    public static final class AnonymizationBuilder {
        private UUID id;
        private OffsetDateTime created;
        private UUID documentId;
        private String originalText;
        private String anonymizedText;
        private String anonymization_level;
        private List<ChangedTerm> changedTerms;

        public AnonymizationBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        public AnonymizationBuilder documentId(final UUID documentId) {
            this.documentId = documentId;
            return this;
        }

        public AnonymizationBuilder originalText(final String originalText) {
            this.originalText = originalText;
            return this;
        }

        public AnonymizationBuilder anonymizedText(final String anonymizedText) {
            this.anonymizedText = anonymizedText;
            return this;
        }

        public AnonymizationBuilder anonymization_level(final String anonymization_level) {
            this.anonymization_level = anonymization_level;
            return this;
        }

        public AnonymizationBuilder changedTerms(final List<ChangedTerm> changedTerms) {
            this.changedTerms = changedTerms;
            return this;
        }

        public AnonymizationBuilder created(final OffsetDateTime created) {
            this.created = created;
            return this;
        }

        public static AnonymizationDto.AnonymizationBuilder fromDao(final AnonymizationDao dao) {
            return new AnonymizationBuilder()
                .id(dao.getId())
                .created(dao.getCreated())
                .documentId(dao.getDocumentId())
                .originalText(dao.getOriginalText())
                .anonymizedText(dao.getAnonymizedText())
                .anonymization_level(dao.getAnonymization_level())
                .changedTerms(dao.getChangedTerms());
        }

        public static AnonymizationDto.AnonymizationBuilder fromDto(final AnonymizationDto dto) {
            return new AnonymizationDto.AnonymizationBuilder()
                .id(dto.getId())
                .created(dto.getCreated())
                .documentId(dto.getDocumentId())
                .originalText(dto.getOriginalText())
                .anonymizedText(dto.getAnonymizedText())
                .anonymization_level(dto.getAnonymization_level())
                .changedTerms(dto.getChangedTerms());
        }

        public AnonymizationDto create() {
            return new AnonymizationDto(id,
                created,
                documentId,
                originalText,
                anonymizedText,
                anonymization_level,
                changedTerms);
        }

        public Optional<AnonymizationDto> build() {
            if (id == null) {
                this.id = UUID.randomUUID();
            }
            if (created == null) {
                this.created = OffsetDateTime.now();
            }
            if (documentId == null || originalText == null || anonymizedText == null
                || anonymization_level== null || changedTerms == null) {
                return Optional.empty();
            }
            else {
                return Optional.of(create());
            }
        }
    }
}
