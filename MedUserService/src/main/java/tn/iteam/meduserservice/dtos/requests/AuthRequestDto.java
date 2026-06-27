package tn.iteam.meduserservice.dtos.requests;

import jakarta.validation.constraints.Email;
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
public class AuthRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
