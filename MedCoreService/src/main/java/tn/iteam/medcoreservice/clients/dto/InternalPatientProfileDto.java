package tn.iteam.medcoreservice.clients.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalPatientProfileDto {
    private String id;
    private String functionalId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String socialSecurityNumber;
    private String bloodType;
    private String profileImageUrl;
}
