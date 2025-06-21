package oopsops.app.anonymization.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import oopsops.app.anonymization.dao.AnonymizationDao;

@Repository
public interface AnonymizationRepository extends JpaRepository<AnonymizationDao, UUID> {
}
