package oopsops.app.anonymization.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import oopsops.app.anonymization.entity.AnonymizationEntity;

@Repository
public interface AnonymizationRepository extends JpaRepository<AnonymizationEntity, UUID> {
    Optional<AnonymizationEntity> findByDocumentId(UUID documentId);

}
