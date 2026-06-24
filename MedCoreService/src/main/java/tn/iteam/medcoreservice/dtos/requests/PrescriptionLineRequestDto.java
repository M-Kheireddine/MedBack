package tn.iteam.medcoreservice.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PrescriptionLineRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String medicationId;

    @NotBlank
    private String dosage;

    @NotBlank
    private String duration;
}
