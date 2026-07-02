package tn.iteam.meduserservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class PatientDto extends UserResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String functionalId;
    private LocalDate birthDate;
    private String socialSecurityNumber;
    private String bloodType;
    private Long totalAppointments;
    private Long totalPrescriptions;
}
