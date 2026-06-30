package tn.iteam.medcoreservice.dtos.requests;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
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
    @FutureOrPresent
    private LocalDateTime startDateTime;

    @NotNull
    @FutureOrPresent
    private LocalDateTime endDateTime;

    @NotBlank
    private String reason;

    @Email
    private String recipientEmail;

    @AssertTrue(message = "endDateTime must be after startDateTime")
    public boolean isDateRangeValid() {
        return startDateTime == null
                || endDateTime == null
                || endDateTime.isAfter(startDateTime);
    }
}
