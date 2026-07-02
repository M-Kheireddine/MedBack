package tn.iteam.medcoreservice.clients.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalDoctorProfileDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String specialty;
    private String phoneNumber;
    private String clinicAddress;
    private String medicalLicenseNumber;
    private String profileImageUrl;
}
