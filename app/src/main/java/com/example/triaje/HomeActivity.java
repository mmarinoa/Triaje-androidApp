package com.example.triaje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.triaje.session.SessionManager;
import com.example.triaje.config.ApiConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private int pacienteId = -1;
    private String userName = "";
    private String userDni = "";
    private String userEmail = "";

    private TextView tvWelcome;
    private TextView tvPatientInfo;

    private TextInputLayout tilHomeReason;
    private TextInputEditText etHomeReason;

    private MaterialButton btnSendDoctor;
    private MaterialButton btnLogout;

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvPatientInfo = findViewById(R.id.tv_patient_info);

        tilHomeReason = findViewById(R.id.til_home_reason);
        etHomeReason = findViewById(R.id.et_home_reason);

        btnSendDoctor = findViewById(R.id.btn_send_doctor);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void loadUserData() {
        Intent intent = getIntent();

        pacienteId = intent.getIntExtra("PACIENTE_ID", sessionManager.getPacienteId());
        userName = intent.getStringExtra("USER_NAME");
        userDni = intent.getStringExtra("USER_DNI");
        userEmail = intent.getStringExtra("USER_EMAIL");

        if (userName == null || userName.trim().isEmpty()) {
            userName = sessionManager.getUserName();
        }

        if (userDni == null || userDni.trim().isEmpty()) {
            userDni = sessionManager.getUserDni();
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            userEmail = sessionManager.getUserEmail();
        }

        if (userName == null || userName.trim().isEmpty()) {
            userName = "Paciente";
        }

        if (userDni == null) {
            userDni = "";
        }

        if (userEmail == null) {
            userEmail = "";
        }

        tvWelcome.setText("Hola, " + userName);

        String patientInfo = "DNI: " + userDni;

        if (!userEmail.isEmpty()) {
            patientInfo += "\nEmail: " + userEmail;
        }

        tvPatientInfo.setText(patientInfo);
    }

    private void setupListeners() {
        btnSendDoctor.setOnClickListener(view -> validateAndSend());
        btnLogout.setOnClickListener(view -> logout());
    }

    private void validateAndSend() {
        clearErrors();

        String reason = getText(etHomeReason);

        if (TextUtils.isEmpty(reason)) {
            tilHomeReason.setError("Introduce el motivo de la consulta");
            etHomeReason.requestFocus();
            return;
        }

        if (pacienteId == -1) {
            Toast.makeText(
                    this,
                    "No se pudo identificar al paciente. Inicia sesión de nuevo.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        String accessToken = sessionManager.getAccessToken();

        if (accessToken.isEmpty()) {
            Toast.makeText(
                    this,
                    "Sesión caducada o no iniciada. Inicia sesión de nuevo.",
                    Toast.LENGTH_LONG
            ).show();
            logout();
            return;
        }

        createConsultation(reason, accessToken);
    }

    private void createConsultation(String reason, String accessToken) {
        btnSendDoctor.setEnabled(false);
        btnSendDoctor.setText("Enviando...");

        JSONObject body = new JSONObject();

        try {
            body.put("motivo", reason);
        } catch (JSONException exception) {
            resetSendButton();
            Toast.makeText(this, "Error preparando la consulta", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.CONSULTAS_URL,
                body,
                response -> {
                    resetSendButton();

                    String message = response.optString("message", "Consulta creada correctamente");
                    Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();

                    JSONObject consulta = response.optJSONObject("consulta");

                    if (consulta == null) {
                        etHomeReason.setText("");
                        return;
                    }

                    int consultaId = consulta.optInt("id", -1);
                    String motivo = consulta.optString("motivo", "");
                    String estado = consulta.optString("estado", "pendiente");
                    String categoria = consulta.optString("categoria", "");
                    int prioridadIa = consulta.optInt("prioridad_ia", -1);
                    String fechaCreacion = consulta.optString("fecha_creacion", "");

                    Intent intent = new Intent(HomeActivity.this, DetalleConsultaActivity.class);
                    intent.putExtra("CONSULTA_ID", consultaId);
                    intent.putExtra("CONSULTA_MOTIVO", motivo);
                    intent.putExtra("CONSULTA_ESTADO", estado);
                    intent.putExtra("CONSULTA_CATEGORIA", categoria);
                    intent.putExtra("CONSULTA_PRIORIDAD_IA", prioridadIa);
                    intent.putExtra("CONSULTA_FECHA_CREACION", fechaCreacion);
                    startActivity(intent);

                    etHomeReason.setText("");
                },
                error -> {
                    resetSendButton();

                    String errorMessage = getVolleyErrorMessage(error.networkResponse);
                    Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        logout();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private String getVolleyErrorMessage(NetworkResponse networkResponse) {
        if (networkResponse == null || networkResponse.data == null) {
            return "No se pudo conectar con el servidor. Comprueba que Django está encendido.";
        }

        try {
            String responseBody = new String(networkResponse.data, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(responseBody);

            if (jsonObject.has("error")) {
                return jsonObject.getString("error");
            }

            if (jsonObject.has("errors")) {
                return jsonObject.getJSONObject("errors").toString();
            }

            return "Error al enviar la consulta.";

        } catch (Exception exception) {
            return "Error al enviar la consulta.";
        }
    }

    private void resetSendButton() {
        btnSendDoctor.setEnabled(true);
        btnSendDoctor.setText("Enviar consulta");
    }

    private void logout() {
        sessionManager.clearSession();

        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearErrors() {
        tilHomeReason.setError(null);
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }
}