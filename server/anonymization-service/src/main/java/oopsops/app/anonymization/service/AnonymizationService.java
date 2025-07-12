package oopsops.app.anonymization.service;

import java.util.List;
import java.util.UUID;
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
        AnonymizationEntity entity;

        if (dto.id() != null && anonymizationRepository.existsById(dto.id())) {
            entity = anonymizationRepository.findById(dto.id()).orElseThrow();
            entity.setCreated(dto.created());
            entity.setDocumentId(dto.documentId());
            entity.setOriginalText(dto.originalText());
            entity.setAnonymizedText(dto.anonymizedText());
            entity.setAnonymization_level(dto.anonymization_level());
            entity.setChangedTerms(dto.changedTerms());
        } else {
            entity = dto.toDao();
        }

        AnonymizationEntity saved = anonymizationRepository.save(entity);
        return AnonymizationDto.fromDao(saved);
    }

    public AnonymizationDto getById(UUID id) {
        return anonymizationRepository.findById(id)
                .map(AnonymizationDto::fromDao)
                .orElseThrow(() -> new RuntimeException("Anonymization not found"));
    }

    public Optional<AnonymizationDto> findByDocumentId(UUID documentId) {
        return anonymizationRepository.findByDocumentId(documentId)
                .map(AnonymizationDto::fromDao);
    }
}
