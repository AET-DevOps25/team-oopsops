package oopsops.app.document.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import oopsops.app.document.entity.Document;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findAllByUserId(UUID userId);
    Optional<Document> findByIdAndUserId(UUID id, UUID userId);

}
