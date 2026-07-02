package tn.iteam.meduserservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
@Table(
        name = "patients",
        indexes = {
                @Index(name = "idx_patients_functional_id", columnList = "functional_id")
        }
)
public class PatientEntity extends UserEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "functional_id", unique = true, length = 15)
    private String functionalId;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String socialSecurityNumber;

    private String bloodType;
}
