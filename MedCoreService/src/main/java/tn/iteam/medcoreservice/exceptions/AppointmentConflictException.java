package tn.iteam.medcoreservice.exceptions;

public class AppointmentConflictException extends RuntimeException {
    public AppointmentConflictException(String message) {
        super(message);
    }
}
