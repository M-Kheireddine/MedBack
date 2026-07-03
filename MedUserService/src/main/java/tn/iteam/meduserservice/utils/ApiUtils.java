package tn.iteam.meduserservice.utils;

public final class ApiUtils {
    private static final String VERSION_1 = "/v1";

    private static final String AUTH_API = "/auth";
    private static final String ADMIN_API = "/admin";
    private static final String PUBLIC_API = "/public";
    private static final String INTERNAL_API = "/internal";
    private static final String USER_API = "/users";
    private static final String DOCTOR_API = "/doctors";
    private static final String PATIENT_API = "/patients";
    private static final String PROFILE_API = "/profile";
    private static final String FILE_API = "/files";

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
    public static final String API_PUBLIC_GET_ALL_DOCTORS = VERSION_1 + PUBLIC_API + DOCTOR_API;
    public static final String API_PUBLIC_GET_DOCTOR_BY_ID = VERSION_1 + PUBLIC_API + DOCTOR_API + "/{doctorId}";
    public static final String API_ADMIN_GET_DOCTOR_PROFILE = VERSION_1 + ADMIN_API + DOCTOR_API + "/{doctorId}/profile";
    public static final String API_ADMIN_GET_DOCTOR_SUMMARY = VERSION_1 + ADMIN_API + DOCTOR_API + "/{doctorId}/summary";
    public static final String API_GET_ALL_PATIENTS = VERSION_1 + ADMIN_API + PATIENT_API;
    public static final String API_GET_PATIENT_BY_ID = VERSION_1 + ADMIN_API + PATIENT_API + "/{patientId}";
    public static final String API_ARCHIVE_PATIENT = VERSION_1 + ADMIN_API + PATIENT_API + "/{patientId}/archive";
    public static final String API_UNARCHIVE_PATIENT = VERSION_1 + ADMIN_API + PATIENT_API + "/{patientId}/unarchive";
    public static final String API_GET_PATIENT_PROFILE = VERSION_1 + ADMIN_API + PATIENT_API + "/{patientId}/profile";
    public static final String API_GET_PATIENT_SUMMARY = VERSION_1 + ADMIN_API + PATIENT_API + "/{patientId}/summary";
    public static final String API_PROFILE_GET_DOCTOR = VERSION_1 + PROFILE_API + DOCTOR_API + "/{doctorId}";
    public static final String API_PROFILE_GET_PATIENT = VERSION_1 + PROFILE_API + PATIENT_API + "/{patientId}";
    public static final String API_UPLOAD_DOCTOR_PROFILE_IMAGE = VERSION_1 + PROFILE_API + DOCTOR_API + "/{doctorId}/image";
    public static final String API_UPLOAD_PATIENT_PROFILE_IMAGE = VERSION_1 + PROFILE_API + PATIENT_API + "/{patientId}/image";
    public static final String API_GET_PROFILE_IMAGE = VERSION_1 + FILE_API + PROFILE_API + "/{userId}";
    public static final String API_INTERNAL_GET_DOCTOR_PROFILE = VERSION_1 + INTERNAL_API + DOCTOR_API + "/{doctorId}/profile";
    public static final String API_INTERNAL_GET_PATIENT_PROFILE = VERSION_1 + INTERNAL_API + PATIENT_API + "/{patientId}/profile";

    private ApiUtils() {
    }
}
