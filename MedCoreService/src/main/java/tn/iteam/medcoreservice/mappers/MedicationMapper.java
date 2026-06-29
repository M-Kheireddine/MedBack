package tn.iteam.medcoreservice.mappers;

import org.springframework.stereotype.Component;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.models.Medication;

@Component
public class MedicationMapper {
    public Medication toMedication(MedicationRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        return Medication.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .category(requestDto.getCategory())
                .laboratory(requestDto.getLaboratory())
                .imageUrl(requestDto.getImageUrl())
                .build();
    }

    public MedicationResponseDto toMedicationResponseDto(Medication medication) {
        if (medication == null) {
            return null;
        }

        return MedicationResponseDto.builder()
                .id(medication.getId())
                .name(medication.getName())
                .description(medication.getDescription())
                .category(medication.getCategory())
                .laboratory(medication.getLaboratory())
                .imageUrl(medication.getImageUrl())
                .build();
    }
}
