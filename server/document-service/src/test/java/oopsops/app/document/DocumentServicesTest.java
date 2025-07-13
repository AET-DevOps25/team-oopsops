package oopsops.app.document;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockMultipartFile;

import oopsops.app.document.entity.Document;
import oopsops.app.document.exception.InvalidFileTypeException;
import oopsops.app.document.repository.DocumentRepository;
import oopsops.app.document.service.DocumentService;
import oopsops.app.document.service.PdfParsingService;
import oopsops.app.document.service.StorageService;

@SpringBootTest
@ActiveProfiles("test")
class DocumentServicesTest {

  @MockitoBean StorageService storageService;
  @MockitoBean PdfParsingService pdfParsingService;
  @MockitoBean DocumentRepository documentRepository;

  @Autowired
  DocumentService documentService;

  private UUID userId;
  private MockMultipartFile pdf;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    pdf = new MockMultipartFile("file","foo.pdf","application/pdf","dummy".getBytes());

    given(documentRepository.save(any(Document.class)))
      .willAnswer(inv -> {
        Document d = inv.getArgument(0);
        if (d.getId() == null) d.setId(UUID.randomUUID());
        return d;
      });
  }

  @Test
  @DisplayName("Happy path: PDF uploaded & processed")
  void validPdf_shouldProcessSuccessfully() {
    given(storageService.store(pdf)).willReturn("file:///tmp/foo.pdf");
    given(pdfParsingService.extractText(any(Path.class))).willReturn("extracted text");

    Document result = documentService.uploadAndProcess(userId, pdf);

    assertThat(result.getStatus()).isEqualTo("PROCESSED");
    assertThat(result.getFileUrl()).isEqualTo("file:///tmp/foo.pdf");
    assertThat(result.getDocumentText().getText()).isEqualTo("extracted text");

    then(storageService).should().store(pdf);
    then(pdfParsingService).should().extractText(any());
    then(documentRepository).should(times(2)).save(any());
  }

  @Test
  @DisplayName("Uploading non-PDF throws InvalidFileTypeException")
  void nonPdf_shouldThrowInvalidFileType() {
    var badFile = new MockMultipartFile("file","bar.pdf","text/plain",new byte[0]);

    assertThatThrownBy(() -> documentService.uploadAndProcess(userId, badFile))
      .isInstanceOf(InvalidFileTypeException.class)
      .hasMessage("Only PDFs allowed");

    then(storageService).shouldHaveNoInteractions();
    then(pdfParsingService).shouldHaveNoInteractions();
    then(documentRepository).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("Storage failure bubbles up")
  void storageFailure_shouldBubbleUp() {
    given(storageService.store(pdf)).willThrow(new RuntimeException("disk full"));

    assertThatThrownBy(() -> documentService.uploadAndProcess(userId, pdf))
      .hasMessage("disk full");

    then(pdfParsingService).shouldHaveNoInteractions();
    then(documentRepository).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("Parsing failure bubbles up after initial save")
  void parsingFailure_shouldBubbleUp() {
    given(storageService.store(pdf)).willReturn("file:///tmp/foo.pdf");
    given(pdfParsingService.extractText(any())).willThrow(new RuntimeException("parse error"));

    assertThatThrownBy(() -> documentService.uploadAndProcess(userId, pdf))
      .hasMessage("parse error");

    then(documentRepository).should(times(1)).save(any());
  }
}
