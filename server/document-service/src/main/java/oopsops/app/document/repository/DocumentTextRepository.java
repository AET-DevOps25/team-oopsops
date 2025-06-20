package oopsops.app.document.repository;

import oopsops.app.document.entity.DocumentText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface DocumentTextRepository extends JpaRepository<DocumentText, UUID> {}