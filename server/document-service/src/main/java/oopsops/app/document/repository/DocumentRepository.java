package oopsops.app.document.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import oopsops.app.document.entity.Document;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {}
