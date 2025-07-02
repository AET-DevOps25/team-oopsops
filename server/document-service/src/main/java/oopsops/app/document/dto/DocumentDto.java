package oopsops.app.document.dto;

import java.time.Instant;
import java.util.UUID;

public record DocumentDto(
    UUID id,
    UUID userId,
    String fileName,
    String fileUrl,
    String status,
    Instant uploadDate,
    String documentText
) {}