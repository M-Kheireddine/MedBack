package tn.iteam.meduserservice.utils;

public class ApiUtils {
    private static final String VERSION_1 = "/v1";

    private static final String AUTH_API = "/auth";
    private static final String ADMIN_API = "/admin";
    private static final String USER_API = "/users";
    private static final String DOCTOR_API = "/doctors";
    private static final String PATIENT_API = "/patients";

    public static final String API_REGISTER_ADMIN = VERSION_1 + AUTH_API + "/register/admin";
    public static final String API_REGISTER_PATIENT = VERSION_1 + AUTH_API + "/register/patient";
    public static final String API_LOGIN = VERSION_1 + AUTH_API + "/login";
    public static final String API_GET_ALL_USERS = VERSION_1 + USER_API;
    public static final String API_GET_USER_BY_ID = VERSION_1 + USER_API + "/{userId}";
    public static final String API_CREATE_DOCTOR = VERSION_1 + ADMIN_API + DOCTOR_API;
    public static final String API_GET_ALL_DOCTORS = VERSION_1 + ADMIN_API + DOCTOR_API;
    public static final String API_GET_DOCTOR_BY_ID = VERSION_1 + ADMIN_API + DOCTOR_API + "/{doctorId}";
    public static final String API_UPDATE_DOCTOR = VERSION_1 + ADMIN_API + DOCTOR_API + "/{doctorId}";
    public static final String API_DELETE_DOCTOR = VERSION_1 + ADMIN_API + DOCTOR_API + "/{doctorId}";
    public static final String API_GET_ALL_PATIENTS = VERSION_1 + ADMIN_API + PATIENT_API;
    public static final String API_GET_PATIENT_BY_ID = VERSION_1 + ADMIN_API + PATIENT_API + "/{patientId}";
    public static final String API_ARCHIVE_PATIENT = VERSION_1 + ADMIN_API + PATIENT_API + "/{patientId}/archive";
}
