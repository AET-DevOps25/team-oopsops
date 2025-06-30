package oopsops.app.anonymization.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import oopsops.app.anonymization.entity.AnonymizationEntity;
import oopsops.app.anonymization.dto.AnonymizationDto;
import oopsops.app.anonymization.repository.AnonymizationRepository;

@Service
public class AnonymizationService {

    private final AnonymizationRepository anonymizationRepository;
    public AnonymizationService(AnonymizationRepository anonymizationRepository) {
        this.anonymizationRepository = anonymizationRepository;
    }

    public List<AnonymizationEntity> getAllAnonymizations() {
        return anonymizationRepository.findAll();
    }

    public AnonymizationDto save(AnonymizationDto dto) {

        return AnonymizationDto.fromDao(anonymizationRepository.save(dto.toDao()));
    }
}
