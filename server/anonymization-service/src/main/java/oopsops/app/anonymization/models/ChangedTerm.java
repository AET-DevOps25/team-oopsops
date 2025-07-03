package oopsops.app.anonymization.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangedTerm {
    private String original;
    private String anonymized;

    public ChangedTerm() {
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(final String original) {
        this.original = original;
    }

    public String getAnonymized() {
        return anonymized;
    }

    public void setAnonymized(final String anonymized) {
        this.anonymized = anonymized;
    }
}
