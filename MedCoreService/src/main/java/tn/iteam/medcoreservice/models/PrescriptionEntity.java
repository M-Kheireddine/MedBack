package tn.iteam.medcoreservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document
public class PrescriptionEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @MongoId
    private String prescriptionId;

    private String doctorId;

    private String patientId;

    private String note;
}