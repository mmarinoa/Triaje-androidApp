package com.example.triaje.config;

public final class ApiConfig {

    private ApiConfig() {
        // Evita que se pueda crear una instancia de esta clase.
    }

    private static final String SERVER_URL = "http://10.0.2.2:8000";
    private static final String API_BASE_URL = SERVER_URL + "/api";

    public static final String REGISTER_URL = API_BASE_URL + "/auth/register/";
    public static final String LOGIN_URL = API_BASE_URL + "/auth/login/";
    public static final String CONSULTAS_URL = API_BASE_URL + "/consultas/";

    public static String getConsultaDetailUrl(int consultaId) {
        return CONSULTAS_URL + consultaId + "/";
    }
}