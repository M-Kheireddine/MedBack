package tn.iteam.medcoreservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.iteam.medcoreservice.models.Appointment;
import tn.iteam.medcoreservice.models.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByDoctorIdOrderByDateTimeAsc(String doctorId);

    List<Appointment> findByPatientIdOrderByDateTimeAsc(String patientId);

    List<Appointment> findByDoctorIdAndDateTimeAndStatus(String doctorId, LocalDateTime dateTime, AppointmentStatus status);

    List<Appointment> findByPatientIdAndDateTimeAndStatus(String patientId, LocalDateTime dateTime, AppointmentStatus status);
}
