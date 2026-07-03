package tn.iteam.medcoreservice.services.specs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationAutocompleteDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.mappers.MedicationDtoMapper;
import tn.iteam.medcoreservice.mappers.MedicationMapper;
import tn.iteam.medcoreservice.models.Medication;
import tn.iteam.medcoreservice.repositories.MedicationRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {

    private static final String PUBLIC_IMAGE_PATH = "/api/uploads/medications";

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private MedicationDtoMapper medicationDtoMapper;

    @TempDir
    Path tempDir;

    private MedicationService medicationService;

    @BeforeEach
    void setUp() {
        medicationService = new MedicationService(medicationRepository, new MedicationMapper(), medicationDtoMapper);
        ReflectionTestUtils.setField(
                medicationService,
                "medicationImageDirectory",
                tempDir.resolve("medications").toString()
        );
        ReflectionTestUtils.setField(
                medicationService,
                "medicationImagePublicPath",
                PUBLIC_IMAGE_PATH
        );
    }

    @Test
    void createMedicationShouldSaveMedicationAndReturnMappedResponse() {
        MedicationRequestDto requestDto = buildRequestDto("  /images/amoxicillin.png  ");

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
    void createAdminMedicationShouldStoreImageAndReturnManagedPublicPath() throws IOException {
        MedicationRequestDto requestDto = buildRequestDto("https://external.example/image.png");
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "pill.png",
                "image/png",
                "binary-image".getBytes()
        );

        when(medicationRepository.save(any(Medication.class))).thenAnswer(invocation -> {
            Medication medication = invocation.getArgument(0);
            medication.setId("med-2");
            return medication;
        });

        MedicationResponseDto response = medicationService.createAdminMedication(requestDto, imageFile);

        ArgumentCaptor<Medication> medicationCaptor = ArgumentCaptor.forClass(Medication.class);
        verify(medicationRepository).save(medicationCaptor.capture());
        Medication savedMedication = medicationCaptor.getValue();

        assertEquals("med-2", response.getId());
        assertNotNull(savedMedication.getImageUrl());
        assertTrue(savedMedication.getImageUrl().startsWith(PUBLIC_IMAGE_PATH + "/"));
        assertTrue(savedMedication.getImageUrl().endsWith(".png"));

        String fileName = savedMedication.getImageUrl().substring((PUBLIC_IMAGE_PATH + "/").length());
        Path storedImage = tempDir.resolve("medications").resolve(fileName);
        assertTrue(Files.exists(storedImage));
        assertEquals("binary-image", Files.readString(storedImage));
    }

    @Test
    void createAdminMedicationShouldThrowWhenUploadedFileIsNotAnImage() {
        MedicationRequestDto requestDto = buildRequestDto(null);
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "notes.txt",
                "text/plain",
                "invalid".getBytes()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> medicationService.createAdminMedication(requestDto, imageFile)
        );

        assertEquals("Only JPEG, PNG, WEBP, and GIF images are allowed for medication uploads.", exception.getMessage());
        verifyNoInteractions(medicationRepository);
    }

    @Test
    void getAllMedicationsShouldReturnSortedMappedResponses() {
        Medication zyrtec = medication("med-3", "zyrtec", "/images/zyrtec.png");
        Medication aspirin = medication("med-4", "Aspirin", "/images/aspirin.png");
        Medication doliprane = medication("med-5", "doliprane", "/images/doliprane.png");

        when(medicationRepository.findAll()).thenReturn(List.of(zyrtec, aspirin, doliprane));

        List<MedicationResponseDto> response = medicationService.getAllMedications();

        assertEquals(3, response.size());
        assertEquals("Aspirin", response.get(0).getName());
        assertEquals("doliprane", response.get(1).getName());
        assertEquals("zyrtec", response.get(2).getName());
    }

    @Test
    void getMedicationByIdShouldReturnMappedResponseWhenMedicationExists() {
        Medication medication = medication("med-42", "Ibuprofen", "/images/ibuprofen.png");

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

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> medicationService.getMedicationById("missing-medication")
        );

        assertEquals("Medication not found with id: missing-medication", exception.getMessage());
    }

    @Test
    void updateAdminMedicationShouldKeepExistingImageWhenRequestImageUrlIsBlankAndNoFile() {
        Medication existingMedication = medication("med-10", "Ibuprofen", "/images/existing.png");
        MedicationRequestDto requestDto = MedicationRequestDto.builder()
                .name("Ibuprofen Forte")
                .description("<p>Updated description</p>")
                .category("Analgesic")
                .laboratory("Bayer")
                .imageUrl("   ")
                .build();

        when(medicationRepository.findById("med-10")).thenReturn(Optional.of(existingMedication));
        when(medicationRepository.save(any(Medication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MedicationResponseDto response = medicationService.updateAdminMedication("med-10", requestDto, null);

        assertEquals("Ibuprofen Forte", existingMedication.getName());
        assertEquals("/images/existing.png", existingMedication.getImageUrl());
        assertEquals("/images/existing.png", response.getImageUrl());
    }

    @Test
    void updateAdminMedicationShouldReplaceManagedImageAndDeletePreviousStoredFile() throws IOException {
        Path storageDirectory = tempDir.resolve("medications");
        Files.createDirectories(storageDirectory);
        Files.writeString(storageDirectory.resolve("old-image.png"), "old-image-content");

        Medication existingMedication = medication("med-11", "Paracetamol", PUBLIC_IMAGE_PATH + "/old-image.png");
        MedicationRequestDto requestDto = MedicationRequestDto.builder()
                .name("Paracetamol Updated")
                .description("<p>Updated description</p>")
                .category("Analgesic")
                .laboratory("Sanofi")
                .imageUrl(" https://cdn.example.com/new-image.png ")
                .build();

        when(medicationRepository.findById("med-11")).thenReturn(Optional.of(existingMedication));
        when(medicationRepository.save(any(Medication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MedicationResponseDto response = medicationService.updateAdminMedication("med-11", requestDto, null);

        assertEquals("Paracetamol Updated", response.getName());
        assertEquals("https://cdn.example.com/new-image.png", response.getImageUrl());
        assertFalse(Files.exists(storageDirectory.resolve("old-image.png")));
    }

    @Test
    void deleteMedicationShouldDeleteManagedImageAndRepositoryEntry() throws IOException {
        Path storageDirectory = tempDir.resolve("medications");
        Files.createDirectories(storageDirectory);
        Files.writeString(storageDirectory.resolve("to-delete.png"), "delete-me");

        Medication medication = medication("med-12", "Cough Syrup", PUBLIC_IMAGE_PATH + "/to-delete.png");
        when(medicationRepository.findById("med-12")).thenReturn(Optional.of(medication));

        medicationService.deleteMedication("med-12");

        verify(medicationRepository).delete(medication);
        assertFalse(Files.exists(storageDirectory.resolve("to-delete.png")));
    }

    @Test
    void searchMedicationsShouldUseFindAllWhenQueryIsBlank() {
        Medication vitaminC = medication("med-13", "vitamin c", "/images/vitamin-c.png");
        Medication aspirin = medication("med-14", "Aspirin", "/images/aspirin.png");

        when(medicationRepository.findAll()).thenReturn(List.of(vitaminC, aspirin));

        List<MedicationResponseDto> response = medicationService.searchMedications("   ");

        verify(medicationRepository).findAll();
        verify(medicationRepository, never()).search(any());
        assertEquals(List.of("Aspirin", "vitamin c"), response.stream().map(MedicationResponseDto::getName).toList());
    }

    @Test
    void searchMedicationsShouldUseQuotedRegexWhenQueryIsProvided() {
        Medication medication = medication("med-15", "Vitamin C+", "/images/vitamin-c.png");

        when(medicationRepository.search(eq("\\Qvit+\\E"))).thenReturn(List.of(medication));

        List<MedicationResponseDto> response = medicationService.searchMedications("  vit+ ");

        verify(medicationRepository).search("\\Qvit+\\E");
        assertEquals(1, response.size());
        assertEquals("Vitamin C+", response.get(0).getName());
    }

    @Test
    void autocompleteMedicationsShouldReturnEmptyListWhenQueryIsBlank() {
        List<MedicationAutocompleteDto> response = medicationService.autocompleteMedications("   ");

        assertTrue(response.isEmpty());
        verify(medicationRepository, never()).autocomplete(any(), any());
    }

    @Test
    void autocompleteMedicationsShouldReturnMappedSuggestionsWhenQueryIsProvided() {
        Medication aspirin = medication("med-16", "Aspirin", null);
        Medication vitamin = medication("med-17", "Vitamin C+", null);

        when(medicationRepository.autocomplete(eq("\\Qvit+\\E"), eq(PageRequest.of(0, 10))))
                .thenReturn(List.of(aspirin, vitamin));

        List<MedicationAutocompleteDto> response = medicationService.autocompleteMedications(" vit+ ");

        verify(medicationRepository).autocomplete("\\Qvit+\\E", PageRequest.of(0, 10));
        assertEquals(2, response.size());
        assertEquals("med-16", response.get(0).getId());
        assertEquals("Aspirin", response.get(0).getName());
        assertEquals("Analgesic", response.get(0).getCategory());
        assertEquals("med-17", response.get(1).getId());
        assertEquals("Vitamin C+", response.get(1).getName());
        assertEquals("Analgesic", response.get(1).getCategory());
    }

    @Test
    void updateMedicationShouldThrowWhenMedicationDoesNotExist() {
        MedicationRequestDto requestDto = buildRequestDto(null);
        when(medicationRepository.findById("unknown-id")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> medicationService.updateMedication("unknown-id", requestDto)
        );
    }

    private MedicationRequestDto buildRequestDto(String imageUrl) {
        return MedicationRequestDto.builder()
                .name("Amoxicillin")
                .description("<p>Antibiotic treatment</p>")
                .category("Antibiotic")
                .laboratory("Pfizer")
                .imageUrl(imageUrl)
                .build();
    }

    private Medication medication(String id, String name, String imageUrl) {
        return Medication.builder()
                .id(id)
                .name(name)
                .description("<p>Description</p>")
                .category("Analgesic")
                .laboratory("Bayer")
                .imageUrl(imageUrl)
                .build();
    }
}
