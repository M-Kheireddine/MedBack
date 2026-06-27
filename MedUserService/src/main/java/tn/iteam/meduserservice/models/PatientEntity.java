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
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "patients")
public class PatientEntity extends UserEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String socialSecurityNumber;

    private String bloodType;
}
