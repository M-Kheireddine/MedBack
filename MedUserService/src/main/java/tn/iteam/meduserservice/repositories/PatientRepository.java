package tn.iteam.meduserservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.iteam.meduserservice.models.PatientEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, UUID> {
    Optional<PatientEntity> findByFunctionalId(String functionalId);

    boolean existsByFunctionalId(String functionalId);

    boolean existsBySocialSecurityNumber(String socialSecurityNumber);

    boolean existsBySocialSecurityNumberAndIdNot(String socialSecurityNumber, UUID id);
}
