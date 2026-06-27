package tn.iteam.meduserservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class DoctorResponseDto extends UserResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String specialty;
    private String phoneNumber;
    private String clinicAddress;
    private String medicalLicenseNumber;
}
