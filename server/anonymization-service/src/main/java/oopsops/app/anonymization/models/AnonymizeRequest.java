package oopsops.app.anonymization.models;

public class AnonymizeRequest {

    private String originalText;
    private String level;

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(final String originalText) {
        this.originalText = originalText;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(final String level) {
        this.level = level;
    }
}
