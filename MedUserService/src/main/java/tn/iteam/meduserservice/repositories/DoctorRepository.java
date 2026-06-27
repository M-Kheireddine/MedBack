package tn.iteam.meduserservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.iteam.meduserservice.models.DoctorEntity;

import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, UUID> {
    boolean existsByMedicalLicenseNumber(String medicalLicenseNumber);

    boolean existsByMedicalLicenseNumberAndIdNot(String medicalLicenseNumber, UUID id);
}
