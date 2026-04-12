package tn.iteam.medcoreservice.utils;

public class ApiUtils {
    private static final String VERSION_1 = "/v1";

    private static final String PRESCRIPTION_API = "/prescriptions";

    public static final String API_GET_ALL_PRESCRIPTIONS = VERSION_1 + PRESCRIPTION_API;
    public static final String API_GET_PRESCRIPTION_BY_ID = VERSION_1 + PRESCRIPTION_API + "/{prescriptionId}";
}
