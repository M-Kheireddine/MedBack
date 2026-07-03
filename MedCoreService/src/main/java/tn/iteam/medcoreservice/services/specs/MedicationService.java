package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationAutocompleteDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.mappers.MedicationDtoMapper;
import tn.iteam.medcoreservice.mappers.MedicationMapper;
import tn.iteam.medcoreservice.models.Medication;
import tn.iteam.medcoreservice.repositories.MedicationRepository;
import tn.iteam.medcoreservice.services.impls.IMedicationService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MedicationService implements IMedicationService {
    private static final int AUTOCOMPLETE_LIMIT = 10;

    @Value("${medback.storage.medication-image-directory:./storage/medications}")
    private String medicationImageDirectory;

    @Value("${medback.storage.medication-image-public-path:/api/uploads/medications}")
    private String medicationImagePublicPath;

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;
    private final MedicationDtoMapper medicationDtoMapper;

    @Override
    public MedicationResponseDto createMedication(MedicationRequestDto requestDto) {
        return createAdminMedication(requestDto, null);
    }

    @Override
    public MedicationResponseDto createAdminMedication(MedicationRequestDto requestDto, MultipartFile imageFile) {
        Medication medication = medicationMapper.toMedication(requestDto);
        medication.setImageUrl(resolveImageUrlForCreate(requestDto, imageFile));
        return medicationMapper.toMedicationResponseDto(medicationRepository.save(medication));
    }

    @Override
    public List<MedicationResponseDto> getAllMedications() {
        return medicationRepository.findAll()
                .stream()
                .sorted((left, right) -> left.getName().compareToIgnoreCase(right.getName()))
                .map(medicationMapper::toMedicationResponseDto)
                .toList();
    }

    @Override
    public MedicationResponseDto getMedicationById(String medicationId) {
        return medicationMapper.toMedicationResponseDto(findMedicationById(medicationId));
    }

    @Override
    public MedicationResponseDto updateMedication(String medicationId, MedicationRequestDto requestDto) {
        return updateAdminMedication(medicationId, requestDto, null);
    }

    @Override
    public MedicationResponseDto updateAdminMedication(String medicationId, MedicationRequestDto requestDto, MultipartFile imageFile) {
        Medication medication = findMedicationById(medicationId);
        String previousImageUrl = medication.getImageUrl();
        String nextImageUrl = resolveImageUrlForUpdate(medication, requestDto, imageFile);

        medication.setName(requestDto.getName());
        medication.setDescription(requestDto.getDescription());
        medication.setCategory(requestDto.getCategory());
        medication.setLaboratory(requestDto.getLaboratory());
        medication.setImageUrl(nextImageUrl);

        Medication savedMedication = medicationRepository.save(medication);

        if (!sameImageUrl(previousImageUrl, nextImageUrl)) {
            deleteStoredImageIfManaged(previousImageUrl, false);
        }

        return medicationMapper.toMedicationResponseDto(savedMedication);
    }

    @Override
    public void deleteMedication(String medicationId) {
        Medication medication = findMedicationById(medicationId);
        medicationRepository.delete(medication);
        deleteStoredImageIfManaged(medication.getImageUrl(), false);
    }

    @Override
    public List<MedicationResponseDto> searchMedications(String query) {
        List<Medication> medications = query == null || query.isBlank()
                ? medicationRepository.findAll()
                : medicationRepository.search(buildRegexQuery(query));
        return medications.stream()
                .sorted((left, right) -> left.getName().compareToIgnoreCase(right.getName()))
                .map(medicationMapper::toMedicationResponseDto)
                .toList();
    }

    @Override
    public List<MedicationDto> getMedicationCatalog(String search) {
        return searchMedications(search).stream()
                .map(medicationDtoMapper::toMedicationDto)
                .toList();
    }

    @Override
    public MedicationDto getMedicationCatalogById(String medicationId) {
        return medicationDtoMapper.toMedicationDto(getMedicationById(medicationId));
    }

    @Override
    public List<MedicationAutocompleteDto> autocompleteMedications(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        return medicationRepository.autocomplete(buildRegexQuery(query), PageRequest.of(0, AUTOCOMPLETE_LIMIT))
                .stream()
                .map(medication -> MedicationAutocompleteDto.builder()
                        .id(medication.getId())
                        .name(medication.getName())
                        .category(medication.getCategory())
                        .build())
                .toList();
    }

    private Medication findMedicationById(String medicationId) {
        return medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + medicationId));
    }

    private String resolveImageUrlForCreate(MedicationRequestDto requestDto, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            return storeImage(imageFile);
        }

        return normalizeText(requestDto.getImageUrl());
    }

    private String resolveImageUrlForUpdate(Medication medication, MedicationRequestDto requestDto, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            return storeImage(imageFile);
        }

        String requestedImageUrl = normalizeText(requestDto.getImageUrl());
        if (requestedImageUrl != null) {
            return requestedImageUrl;
        }

        return medication.getImageUrl();
    }

    private String storeImage(MultipartFile imageFile) {
        ManagedImageFormat imageFormat = resolveImageFormat(imageFile);

        try {
            Path storageDirectory = getStorageDirectory();
            Files.createDirectories(storageDirectory);

            String fileName = UUID.randomUUID() + imageFormat.extension();
            Path targetPath = storageDirectory.resolve(fileName).normalize();
            validateManagedStoragePath(storageDirectory, targetPath);

            try (java.io.InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return medicationImagePublicPath + "/" + fileName;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to store medication image.", exception);
        }
    }

    private ManagedImageFormat resolveImageFormat(MultipartFile imageFile) {
        String contentType = normalizeText(imageFile.getContentType());
        if (contentType == null) {
            throw new IllegalArgumentException("Only image files are allowed for medication uploads.");
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/jpg" -> ManagedImageFormat.JPG;
            case "image/png" -> ManagedImageFormat.PNG;
            case "image/webp" -> ManagedImageFormat.WEBP;
            case "image/gif" -> ManagedImageFormat.GIF;
            default -> throw new IllegalArgumentException("Only JPEG, PNG, WEBP, and GIF images are allowed for medication uploads.");
        };
    }

    private void deleteStoredImageIfManaged(String imageUrl, boolean failOnDeleteError) {
        if (!isManagedImageUrl(imageUrl)) {
            return;
        }

        String fileName = imageUrl.substring(medicationImagePublicPath.length() + 1);
        Path imagePath = getStorageDirectory().resolve(fileName).normalize();
        if (!imagePath.startsWith(getStorageDirectory())) {
            if (failOnDeleteError) {
                throw new IllegalStateException("Medication image path is outside the configured storage directory.");
            }

            log.warn("Skipped deletion for image outside medication storage directory: {}", imagePath);
            return;
        }

        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException exception) {
            if (failOnDeleteError) {
                throw new IllegalStateException("Failed to delete stored medication image.", exception);
            }

            log.warn("Failed to delete stored medication image at {}", imagePath, exception);
        }
    }

    private boolean isManagedImageUrl(String imageUrl) {
        String normalizedImageUrl = normalizeText(imageUrl);
        return normalizedImageUrl != null
                && normalizedImageUrl.startsWith(medicationImagePublicPath + "/");
    }

    private boolean sameImageUrl(String currentImageUrl, String nextImageUrl) {
        String normalizedCurrentImageUrl = normalizeText(currentImageUrl);
        String normalizedNextImageUrl = normalizeText(nextImageUrl);

        if (normalizedCurrentImageUrl == null) {
            return normalizedNextImageUrl == null;
        }

        return normalizedCurrentImageUrl.equals(normalizedNextImageUrl);
    }

    private void validateManagedStoragePath(Path storageDirectory, Path targetPath) {
        if (!targetPath.startsWith(storageDirectory) || !storageDirectory.equals(targetPath.getParent())) {
            throw new IllegalStateException("Medication image path is outside the configured storage directory.");
        }
    }

    private Path getStorageDirectory() {
        return Paths.get(medicationImageDirectory).toAbsolutePath().normalize();
    }

    private String buildRegexQuery(String query) {
        return Pattern.quote(query.trim());
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    private enum ManagedImageFormat {
        JPG(".jpg"),
        PNG(".png"),
        WEBP(".webp"),
        GIF(".gif");

        private final String extension;

        ManagedImageFormat(String extension) {
            this.extension = extension;
        }

        private String extension() {
            return extension;
        }
    }
}
