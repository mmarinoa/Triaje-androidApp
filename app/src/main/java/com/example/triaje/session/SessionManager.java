package com.example.triaje.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "triaje_session";

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_PACIENTE_ID = "paciente_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_DNI = "user_dni";
    private static final String KEY_USER_EMAIL = "user_email";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(
            String accessToken,
            String refreshToken,
            int pacienteId,
            String userName,
            String userDni,
            String userEmail
    ) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putInt(KEY_PACIENTE_ID, pacienteId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_DNI, userDni);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, "");
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, "");
    }

    public int getPacienteId() {
        return sharedPreferences.getInt(KEY_PACIENTE_ID, -1);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getUserDni() {
        return sharedPreferences.getString(KEY_USER_DNI, "");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public boolean isLoggedIn() {
        return !getAccessToken().isEmpty() && getPacienteId() != -1;
    }

    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }
}