package oopsops.app.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import oopsops.app.document.exception.PdfParsingException;
import oopsops.app.document.service.PdfParsingService;

public class PdfParsingServiceTest {

  private final PdfParsingService pdfParsingService = new PdfParsingService();

  @Test
  @DisplayName("extractText returns “Hi!” for sample.pdf")
  void extractText_shouldReturnKnownContent() throws Exception {
    InputStream is = getClass().getResourceAsStream("/sample.pdf");
    Path tmp = Files.createTempFile("sample", ".pdf");
    Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);

    String text = pdfParsingService.extractText(tmp);
    assertThat(text).contains("Hi!");
  }

  @Test
  @DisplayName("extractText on missing file throws PdfParsingException")
  void extractText_whenCorruptFile_shouldThrowPdfParsingException() {
    Path fake = Path.of("nonexistent.pdf");

    assertThatThrownBy(() -> pdfParsingService.extractText(fake))
      .isInstanceOf(PdfParsingException.class)
      .hasMessageContaining("Unable to parse PDF: " + fake);
  }
}
