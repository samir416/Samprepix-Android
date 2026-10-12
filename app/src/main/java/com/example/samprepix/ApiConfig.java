package com.example.samprepix;

public class ApiConfig {

    public static final String LOCAL_BASE_URL =
            "http://10.117.7.190:8080";

    public static final String ONLINE_BASE_URL =
            "http://10.117.7.190:8080";

    public static String BASE_URL =
            LOCAL_BASE_URL;

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getLoginUrl() {
        return BASE_URL + "/login";
    }

    public static String getRegisterUrl() {
        return BASE_URL + "/register";
    }

    public static String getCurrentUserUrl() {
        return BASE_URL + "/me";
    }

    public static String getResumeAnalyzeUrl() {
        return BASE_URL + "/resume/analyze";
    }

    public static String getResumeHistoryUrl() {
        return BASE_URL + "/resume/history";
    }
}