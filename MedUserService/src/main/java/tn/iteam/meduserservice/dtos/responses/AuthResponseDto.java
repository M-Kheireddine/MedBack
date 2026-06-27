package tn.iteam.meduserservice.dtos.responses;

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
public class AuthResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String accessToken;
    private String tokenType;
    private LocalDateTime expiresAt;
    private UserResponseDto user;
}
