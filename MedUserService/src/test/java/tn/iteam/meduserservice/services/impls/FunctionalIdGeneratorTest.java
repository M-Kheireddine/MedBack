package tn.iteam.meduserservice.services.impls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.iteam.meduserservice.repositories.PatientRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FunctionalIdGeneratorTest {

    @Mock
    private PatientRepository patientRepository;

    private FunctionalIdGenerator functionalIdGenerator;

    @BeforeEach
    void setUp() {
        functionalIdGenerator = new FunctionalIdGenerator(patientRepository);
    }

    @Test
    void generatePatientFunctionalIdShouldReturnFormattedValue() {
        when(patientRepository.existsByFunctionalId(anyString())).thenReturn(false);

        String functionalId = functionalIdGenerator.generatePatientFunctionalId();

        assertTrue(functionalId.matches("PAT-\\d{3}-\\d{3}-\\d{3}"));
        verify(patientRepository).existsByFunctionalId(functionalId);
    }

    @Test
    void generatePatientFunctionalIdShouldRetryWhenIdentifierAlreadyExists() {
        when(patientRepository.existsByFunctionalId(anyString())).thenReturn(true, false);

        String functionalId = functionalIdGenerator.generatePatientFunctionalId();

        assertTrue(functionalId.matches("PAT-\\d{3}-\\d{3}-\\d{3}"));
        verify(patientRepository, times(2)).existsByFunctionalId(anyString());
    }
}
