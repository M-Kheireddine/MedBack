package tn.iteam.medcoreservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.iteam.medcoreservice.models.Appointment;
import tn.iteam.medcoreservice.models.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByDoctorIdOrderByStartDateTimeAsc(String doctorId);

    List<Appointment> findByPatientIdOrderByStartDateTimeAsc(String patientId);

    @Query(value = "{ 'doctorId': ?0, 'startDateTime': { $lt: ?2 }, 'endDateTime': { $gt: ?1 } }", sort = "{ 'startDateTime': 1 }")
    List<Appointment> findDoctorAppointmentsInRange(String doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Appointment> findByDoctorIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            String doctorId,
            AppointmentStatus status,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime
    );

    List<Appointment> findByPatientIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            String patientId,
            AppointmentStatus status,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime
    );
}
