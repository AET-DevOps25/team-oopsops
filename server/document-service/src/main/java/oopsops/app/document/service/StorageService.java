package oopsops.app.document.service;

import org.springframework.web.multipart.MultipartFile;
import oopsops.app.document.exception.StorageException;

/**
 * Interface for a service that handles file storage.
 * This service is responsible for storing files and returning their storage identifiers.
 * Implements the Strategy design pattern to allow for different storage implementations (e.g., local file system, cloud storage).
 */
public interface StorageService {
  String store(MultipartFile file) throws StorageException;
}
