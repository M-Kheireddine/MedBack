package tn.iteam.medcoreservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PrescriptionResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String doctorId;
    private String patientId;
    private LocalDateTime createdAt;
    private String doctorNotes;
    private List<PrescriptionLineDto> prescriptionLines;
    private PrescriptionDoctorMetadataDto doctor;
    private PrescriptionPatientMetadataDto patient;
}
