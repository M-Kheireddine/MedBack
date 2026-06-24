package tn.iteam.medcoreservice.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AppointmentRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String doctorId;

    @NotBlank
    private String patientId;

    @NotNull
    @Future
    private LocalDateTime dateTime;

    @NotBlank
    private String reason;

    @Email
    private String recipientEmail;
}
