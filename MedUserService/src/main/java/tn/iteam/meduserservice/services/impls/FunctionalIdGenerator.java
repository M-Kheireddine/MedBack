package tn.iteam.meduserservice.services.impls;

import org.springframework.stereotype.Service;
import tn.iteam.meduserservice.repositories.PatientRepository;

import java.security.SecureRandom;

@Service
public class FunctionalIdGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final PatientRepository patientRepository;

    public FunctionalIdGenerator(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public String generatePatientFunctionalId() {
        String functionalId;

        do {
            functionalId = "PAT-%03d-%03d-%03d".formatted(
                    SECURE_RANDOM.nextInt(1_000),
                    SECURE_RANDOM.nextInt(1_000),
                    SECURE_RANDOM.nextInt(1_000)
            );
        } while (patientRepository.existsByFunctionalId(functionalId));

        return functionalId;
    }
}
