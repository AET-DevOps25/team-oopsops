package oopsops.app.anonymization.models;

import java.util.List;

public class ReplacementRequest {

    private String originalText;
    private List<ChangedTerm> replacements;

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(final String originalText) {
        this.originalText = originalText;
    }

    public List<ChangedTerm> getChangedTerms() {
        return replacements;
    }

    public void setChangedTerms(final List<ChangedTerm> replacements) {
        this.replacements = replacements;
    }
}
