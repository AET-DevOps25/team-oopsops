package oopsops.app.anonymization.entity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import oopsops.app.anonymization.models.ChangedTerm;

@Entity
@Table(name = "anonymization")
public class AnonymizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "created", nullable = false)
    private OffsetDateTime created;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "original_text", nullable = false)
    private String originalText;

    @Column(name = "anonymized_text", nullable = false)
    private String anonymizedText;

    @Column(name =  "anonymization_level")
    private String anonymization_level;

    @Type(JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ChangedTerm> changedTerms;

    public AnonymizationEntity() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(final UUID documentId) {
        this.documentId = documentId;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(final String originalText) {
        this.originalText = originalText;
    }

    public List<ChangedTerm> getChangedTerms() {
        return changedTerms;
    }

    public void setChangedTerms(final List<ChangedTerm> changedTerms) {
        this.changedTerms = changedTerms;
    }

    public String getAnonymization_level() {
        return anonymization_level;
    }

    public void setAnonymization_level(final String anonymization_level) {
        this.anonymization_level = anonymization_level;
    }

    public String getAnonymizedText() {
        return anonymizedText;
    }

    public void setAnonymizedText(final String anonymizedText) {
        this.anonymizedText = anonymizedText;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(final OffsetDateTime created) {
        this.created = created;
    }

    public AnonymizationEntity(final UUID id, final OffsetDateTime created, final UUID documentId, final String originalText, final String anonymizedText, final String anonymization_level, final List<ChangedTerm> changedTerms) {
        this.id = id;
        this.created = created;
        this.documentId = documentId;
        this.originalText = originalText;
        this.anonymizedText = anonymizedText;
        this.anonymization_level = anonymization_level;
        this.changedTerms = changedTerms;
    }
}
