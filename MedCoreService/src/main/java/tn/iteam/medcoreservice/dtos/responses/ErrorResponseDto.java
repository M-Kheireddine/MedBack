package tn.iteam.medcoreservice.dtos.responses;

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
public class ErrorResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
