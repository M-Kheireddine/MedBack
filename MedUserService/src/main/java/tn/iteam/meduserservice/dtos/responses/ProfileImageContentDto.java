package tn.iteam.meduserservice.dtos.responses;

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
public class ProfileImageContentDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private byte[] content;
    private String contentType;
}
