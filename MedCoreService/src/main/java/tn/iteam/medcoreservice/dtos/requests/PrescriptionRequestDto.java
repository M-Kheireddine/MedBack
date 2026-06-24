package tn.iteam.medcoreservice.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PrescriptionRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String doctorId;

    @NotBlank
    private String patientId;

    private String doctorNotes;

    @Valid
    @NotEmpty
    private List<PrescriptionLineRequestDto> prescriptionLines;

    @Email
    private String recipientEmail;
}
