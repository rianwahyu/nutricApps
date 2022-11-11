package com.rigadev.nutricapps.util;

public class NetworkState {

    public static String baseUrl = "http://192.168.1.105:8080/nutricapps_web/";

    public static String locatedStorage = baseUrl+"storages/";

    public static String baseApiUrl = baseUrl+"api/";

    public static String authApiUrl = baseUrl+"api/auth/";

    public static String foodApiUrl = baseUrl+"api/food/";

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getBaseApiUrl() {
        return baseApiUrl;
    }


}
