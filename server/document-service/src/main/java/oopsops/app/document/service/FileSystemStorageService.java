package oopsops.app.document.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import oopsops.app.document.exception.StorageException;

import java.io.IOException;
import java.nio.file.*;

@Service
@Profile({"dev","test"})
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    public FileSystemStorageService(@Value("${storage.location:uploads}") String location) {
        this.rootLocation = Paths.get(location).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage directory", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        String filename = System.nanoTime() + "-" + Path.of(file.getOriginalFilename()).getFileName();
        try {
            Path destination = rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toUri().toString();
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }
}