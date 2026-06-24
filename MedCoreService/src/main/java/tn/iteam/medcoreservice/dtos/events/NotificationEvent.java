package tn.iteam.medcoreservice.dtos.events;

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
public class NotificationEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String type;
    private String recipientEmail;
    private String subject;
    private String message;
}
