package oopsops.app.document.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;

import oopsops.app.document.exception.PdfParsingException;

@Service
public class PdfParsingService {
    
    public String extractText(Path pdfPath) {
        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new PdfParsingException("Unable to parse PDF: " + pdfPath, e);
        }
    }

    public String extractText(InputStream pdfStream) {
        try (PDDocument document = PDDocument.load(pdfStream)) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new PdfParsingException("Unable to parse PDF from stream", e);
        }
    }

}
