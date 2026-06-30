package tn.iteam.medcoreservice.services.specs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.mappers.MedicationMapper;
import tn.iteam.medcoreservice.models.Medication;
import tn.iteam.medcoreservice.repositories.MedicationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    private MedicationService medicationService;

    @BeforeEach
    void setUp() {
        medicationService = new MedicationService(medicationRepository, new MedicationMapper());
    }

    @Test
    void createMedicationShouldSaveMedicationAndReturnMappedResponse() {
        MedicationRequestDto requestDto = MedicationRequestDto.builder()
                .name("Amoxicillin")
                .description("<p>Antibiotic treatment</p>")
                .category("Antibiotic")
                .laboratory("Pfizer")
                .imageUrl("  /images/amoxicillin.png  ")
                .build();

        when(medicationRepository.save(any(Medication.class))).thenAnswer(invocation -> {
            Medication medication = invocation.getArgument(0);
            medication.setId("med-1");
            return medication;
        });

        MedicationResponseDto response = medicationService.createMedication(requestDto);

        ArgumentCaptor<Medication> medicationCaptor = ArgumentCaptor.forClass(Medication.class);
        verify(medicationRepository).save(medicationCaptor.capture());
        Medication savedMedication = medicationCaptor.getValue();

        assertEquals("Amoxicillin", savedMedication.getName());
        assertEquals("<p>Antibiotic treatment</p>", savedMedication.getDescription());
        assertEquals("Antibiotic", savedMedication.getCategory());
        assertEquals("Pfizer", savedMedication.getLaboratory());
        assertEquals("/images/amoxicillin.png", savedMedication.getImageUrl());

        assertEquals("med-1", response.getId());
        assertEquals("Amoxicillin", response.getName());
        assertEquals("/images/amoxicillin.png", response.getImageUrl());
    }

    @Test
    void getMedicationByIdShouldReturnMappedResponseWhenMedicationExists() {
        Medication medication = Medication.builder()
                .id("med-42")
                .name("Ibuprofen")
                .description("<p>Pain relief</p>")
                .category("Analgesic")
                .laboratory("Bayer")
                .imageUrl("/images/ibuprofen.png")
                .build();

        when(medicationRepository.findById("med-42")).thenReturn(Optional.of(medication));

        MedicationResponseDto response = medicationService.getMedicationById("med-42");

        assertEquals("med-42", response.getId());
        assertEquals("Ibuprofen", response.getName());
        assertEquals("Analgesic", response.getCategory());
        assertEquals("/images/ibuprofen.png", response.getImageUrl());
    }

    @Test
    void getMedicationByIdShouldThrowWhenMedicationDoesNotExist() {
        when(medicationRepository.findById("missing-medication")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> medicationService.getMedicationById("missing-medication")
        );
    }
}
