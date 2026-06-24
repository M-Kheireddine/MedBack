package tn.iteam.medchatbootservice.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChatRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String message;

    private String doctorId;
}
