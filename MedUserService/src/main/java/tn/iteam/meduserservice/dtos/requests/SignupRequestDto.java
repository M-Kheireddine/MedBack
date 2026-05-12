package tn.iteam.meduserservice.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String role; // admin, doctor, patient, guest
}
