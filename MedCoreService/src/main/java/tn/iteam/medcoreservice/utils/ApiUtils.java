package tn.iteam.medcoreservice.utils;

public class ApiUtils {
    private static final String VERSION_1 = "/v1";

    private static final String APPOINTMENT_API = "/appointments";
    private static final String MEDICATION_API = "/medications";
    private static final String PRESCRIPTION_API = "/prescriptions";
    private static final String PUBLIC_API = "/public";

    public static final String API_CREATE_APPOINTMENT = VERSION_1 + APPOINTMENT_API;
    public static final String API_GET_ALL_APPOINTMENTS = VERSION_1 + APPOINTMENT_API;
    public static final String API_GET_APPOINTMENT_BY_ID = VERSION_1 + APPOINTMENT_API + "/{appointmentId}";
    public static final String API_GET_APPOINTMENTS_BY_DOCTOR = VERSION_1 + APPOINTMENT_API + "/doctor/{doctorId}";
    public static final String API_GET_APPOINTMENTS_BY_PATIENT = VERSION_1 + APPOINTMENT_API + "/patient/{patientId}";
    public static final String API_UPDATE_APPOINTMENT = VERSION_1 + APPOINTMENT_API + "/{appointmentId}";
    public static final String API_CANCEL_APPOINTMENT = VERSION_1 + APPOINTMENT_API + "/{appointmentId}/cancel";
    public static final String API_COMPLETE_APPOINTMENT = VERSION_1 + APPOINTMENT_API + "/{appointmentId}/complete";

    public static final String API_CREATE_MEDICATION = VERSION_1 + MEDICATION_API;
    public static final String API_GET_ALL_MEDICATIONS = VERSION_1 + MEDICATION_API;
    public static final String API_GET_MEDICATION_BY_ID = VERSION_1 + MEDICATION_API + "/{medicationId}";
    public static final String API_UPDATE_MEDICATION = VERSION_1 + MEDICATION_API + "/{medicationId}";
    public static final String API_DELETE_MEDICATION = VERSION_1 + MEDICATION_API + "/{medicationId}";
    public static final String API_SEARCH_MEDICATIONS = VERSION_1 + MEDICATION_API + "/search";
    public static final String API_PUBLIC_GET_ALL_MEDICATIONS = VERSION_1 + PUBLIC_API + MEDICATION_API;
    public static final String API_PUBLIC_SEARCH_MEDICATIONS = VERSION_1 + PUBLIC_API + MEDICATION_API + "/search";

    public static final String API_CREATE_PRESCRIPTION = VERSION_1 + PRESCRIPTION_API;
    public static final String API_GET_ALL_PRESCRIPTIONS = VERSION_1 + PRESCRIPTION_API;
    public static final String API_GET_PRESCRIPTION_BY_ID = VERSION_1 + PRESCRIPTION_API + "/{prescriptionId}";
    public static final String API_GET_PRESCRIPTIONS_BY_DOCTOR = VERSION_1 + PRESCRIPTION_API + "/doctor/{doctorId}";
    public static final String API_GET_PRESCRIPTIONS_BY_PATIENT = VERSION_1 + PRESCRIPTION_API + "/patient/{patientId}";
    public static final String API_DELETE_PRESCRIPTION = VERSION_1 + PRESCRIPTION_API + "/{prescriptionId}";
}
