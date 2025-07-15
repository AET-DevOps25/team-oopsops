package oopsops.app.anonymization.models;

import java.util.List;

public class ReplacementRequest {

    private String originalText;
    private List<ChangedTerm> changedTerms;

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
}
