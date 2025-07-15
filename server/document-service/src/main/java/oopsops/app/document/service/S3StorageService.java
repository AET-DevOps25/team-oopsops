package oopsops.app.document.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("prod")
public class S3StorageService implements StorageService {

    @Override
    public String store(MultipartFile file) {
        // TBD
        // Placeholder for S3 storage logic
        // throw new UnsupportedOperationException("S3 storage not implemented yet");
        return null;
    }

}
