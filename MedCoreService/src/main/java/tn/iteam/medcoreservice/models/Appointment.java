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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "appointments")
@CompoundIndexes({
        @CompoundIndex(name = "doctor_datetime_idx", def = "{'doctorId': 1, 'dateTime': 1}"),
        @CompoundIndex(name = "patient_datetime_idx", def = "{'patientId': 1, 'dateTime': 1}")
})
public class Appointment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed
    private String doctorId;

    @Indexed
    private String patientId;

    @Indexed
    private LocalDateTime dateTime;

    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    private String reason;
}
