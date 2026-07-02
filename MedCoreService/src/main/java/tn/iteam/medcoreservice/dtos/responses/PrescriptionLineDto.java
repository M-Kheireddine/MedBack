package tn.iteam.medcoreservice.dtos.responses;

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
public class PrescriptionLineDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String medicationId;
    private String medicationName;
    private String dosage;
    private String duration;
}
