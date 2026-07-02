package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.clients.UserProfileClient;
import tn.iteam.medcoreservice.clients.dto.InternalPatientProfileDto;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientIdentifierResolver {
    private final UserProfileClient userProfileClient;

    public String resolvePrimaryPatientId(String patientIdentifier) {
        InternalPatientProfileDto patientProfile = resolvePatientProfile(patientIdentifier);

        if (patientProfile != null && patientProfile.getFunctionalId() != null && !patientProfile.getFunctionalId().isBlank()) {
            return patientProfile.getFunctionalId();
        }

        return patientIdentifier;
    }

    public List<String> resolveCandidatePatientIds(String patientIdentifier) {
        Set<String> candidateIds = new LinkedHashSet<>();
        addIfPresent(candidateIds, patientIdentifier);

        InternalPatientProfileDto patientProfile = resolvePatientProfile(patientIdentifier);
        if (patientProfile != null) {
            addIfPresent(candidateIds, patientProfile.getFunctionalId());
            addIfPresent(candidateIds, patientProfile.getId());
        }

        return List.copyOf(candidateIds);
    }

    private InternalPatientProfileDto resolvePatientProfile(String patientIdentifier) {
        if (patientIdentifier == null || patientIdentifier.isBlank()) {
            return null;
        }

        try {
            return userProfileClient.getPatientProfile(patientIdentifier);
        } catch (Exception exception) {
            log.debug("Unable to resolve patient identifier {}: {}", patientIdentifier, exception.getMessage());
            return null;
        }
    }

    private void addIfPresent(Set<String> candidateIds, String value) {
        if (value != null && !value.isBlank()) {
            candidateIds.add(value.trim());
        }
    }
}
