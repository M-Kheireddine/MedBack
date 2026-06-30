package tn.iteam.medcoreservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "prescriptions")
@CompoundIndexes({
        @CompoundIndex(name = "doctor_created_at_idx", def = "{'doctorId': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "patient_created_at_idx", def = "{'patientId': 1, 'createdAt': -1}")
})
public class Prescription implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed
    private String doctorId;

    @Indexed
    private String patientId;

    @Indexed
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String doctorNotes;

    @Builder.Default
    private List<PrescriptionLine> prescriptionLines = new ArrayList<>();
}
