package tn.iteam.meduserservice.utils;

public class ApiUtils {
    private static final String VERSION_1 = "/v1";

    private static final String USER_API = "/users";

    public static final String API_GET_ALL_USERS = VERSION_1 + USER_API;
    public static final String API_GET_USER_BY_ID = VERSION_1 + USER_API + "/{userId}";

    private static final String AUTH_API = "/auth";
    public static final String API_AUTH_SIGNUP = VERSION_1 + AUTH_API + "/signup";
    public static final String API_AUTH_SIGNIN = VERSION_1 + AUTH_API + "/signin";
}
