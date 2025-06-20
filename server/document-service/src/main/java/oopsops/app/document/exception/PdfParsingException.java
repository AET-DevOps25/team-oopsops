package oopsops.app.document.exception;

public class PdfParsingException extends RuntimeException {
    public PdfParsingException(String message) {
        super(message);
    }

    public PdfParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
