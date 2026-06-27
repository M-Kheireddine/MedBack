package tn.iteam.meduserservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.iteam.meduserservice.models.PatientEntity;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, UUID> {
    boolean existsBySocialSecurityNumber(String socialSecurityNumber);

    boolean existsBySocialSecurityNumberAndIdNot(String socialSecurityNumber, UUID id);
}
