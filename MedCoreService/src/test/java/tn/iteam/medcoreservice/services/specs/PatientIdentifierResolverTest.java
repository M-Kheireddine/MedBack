package tn.iteam.medcoreservice.services.specs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.iteam.medcoreservice.clients.UserProfileClient;
import tn.iteam.medcoreservice.clients.dto.InternalPatientProfileDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientIdentifierResolverTest {

    @Mock
    private UserProfileClient userProfileClient;

    private PatientIdentifierResolver patientIdentifierResolver;

    @BeforeEach
    void setUp() {
        patientIdentifierResolver = new PatientIdentifierResolver(userProfileClient);
    }

    @Test
    void resolvePrimaryPatientIdShouldPreferFunctionalIdWhenAvailable() {
        InternalPatientProfileDto profile = patientProfile("legacy-id", "PAT-123-456-789");
        when(userProfileClient.getPatientProfile("legacy-id")).thenReturn(profile);

        String resolvedId = patientIdentifierResolver.resolvePrimaryPatientId("legacy-id");

        assertEquals("PAT-123-456-789", resolvedId);
    }

    @Test
    void resolvePrimaryPatientIdShouldReturnOriginalIdentifierWhenProfileIsMissing() {
        when(userProfileClient.getPatientProfile("legacy-id")).thenThrow(new RuntimeException("User service unavailable"));

        String resolvedId = patientIdentifierResolver.resolvePrimaryPatientId("legacy-id");

        assertEquals("legacy-id", resolvedId);
    }

    @Test
    void resolvePrimaryPatientIdShouldReturnOriginalBlankIdentifierWhenInputIsBlank() {
        assertEquals(" ", patientIdentifierResolver.resolvePrimaryPatientId(" "));
    }

    @Test
    void resolveCandidatePatientIdsShouldReturnUniqueTrimmedCandidates() {
        InternalPatientProfileDto profile = patientProfile("legacy-id", "PAT-111-222-333");
        when(userProfileClient.getPatientProfile(" legacy-id ")).thenReturn(profile);

        List<String> candidateIds = patientIdentifierResolver.resolveCandidatePatientIds(" legacy-id ");

        assertIterableEquals(List.of("legacy-id", "PAT-111-222-333"), candidateIds);
    }

    @Test
    void resolveCandidatePatientIdsShouldKeepOriginalIdentifierWhenLookupFails() {
        when(userProfileClient.getPatientProfile("legacy-id")).thenThrow(new IllegalStateException("Lookup failed"));

        List<String> candidateIds = patientIdentifierResolver.resolveCandidatePatientIds("legacy-id");

        assertIterableEquals(List.of("legacy-id"), candidateIds);
    }

    @Test
    void resolveCandidatePatientIdsShouldReturnEmptyListForBlankIdentifier() {
        List<String> candidateIds = patientIdentifierResolver.resolveCandidatePatientIds(" ");

        assertIterableEquals(List.of(), candidateIds);
    }

    private InternalPatientProfileDto patientProfile(String id, String functionalId) {
        InternalPatientProfileDto profile = new InternalPatientProfileDto();
        profile.setId(id);
        profile.setFunctionalId(functionalId);
        profile.setFirstName("Ariel");
        profile.setLastName("Richardson");
        return profile;
    }
}
