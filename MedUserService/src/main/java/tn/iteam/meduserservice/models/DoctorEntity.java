package tn.iteam.meduserservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "doctors")
public class DoctorEntity extends UserEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String specialty;

    private String phoneNumber;

    private String clinicAddress;

    @Column(nullable = false, unique = true)
    private String medicalLicenseNumber;
}
