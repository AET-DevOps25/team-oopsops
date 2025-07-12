package oopsops.app.anonymization.models;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record AnonymizationRequestBody(
    String originalText,
    String anonymizedText,
    // UUID userId,
    String level,
    List<ChangedTerm> changedTerms) {

    public AnonymizationRequestBody {
        Objects.requireNonNull(originalText, "originalText must not be null");
        Objects.requireNonNull(anonymizedText, "anonymizedText must not be null");
        Objects.requireNonNull(level, "level must not be null");
        // Objects.requireNonNull(userId, "userId must not be null");
    }
}
