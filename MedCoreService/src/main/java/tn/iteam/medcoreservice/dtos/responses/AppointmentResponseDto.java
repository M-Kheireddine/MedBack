package tn.iteam.medcoreservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.iteam.medcoreservice.models.AppointmentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AppointmentResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String doctorId;
    private String patientId;
    private LocalDateTime dateTime;
    private AppointmentStatus status;
    private String reason;
}
