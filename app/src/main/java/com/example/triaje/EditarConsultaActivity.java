package com.example.triaje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class EditarConsultaActivity extends AppCompatActivity {

    private int consultaId = -1;

    private TextInputLayout tilEditarMotivo;
    private TextInputEditText etEditarMotivo;

    private MaterialButton btnGuardarCambios;
    private MaterialButton btnCancelarEdicion;

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_consulta);

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        initViews();
        loadConsultaData();
        setupListeners();
    }

    private void initViews() {
        tilEditarMotivo = findViewById(R.id.til_editar_motivo);
        etEditarMotivo = findViewById(R.id.et_editar_motivo);

        btnGuardarCambios = findViewById(R.id.btn_guardar_cambios);
        btnCancelarEdicion = findViewById(R.id.btn_cancelar_edicion);
    }

    private void loadConsultaData() {
        consultaId = getIntent().getIntExtra("CONSULTA_ID", -1);
        String motivoActual = getIntent().getStringExtra("CONSULTA_MOTIVO");

        if (motivoActual != null) {
            etEditarMotivo.setText(motivoActual);
            etEditarMotivo.setSelection(motivoActual.length());
        }
    }

    private void setupListeners() {
        btnGuardarCambios.setOnClickListener(view -> validateForm());
        btnCancelarEdicion.setOnClickListener(view -> finish());
    }

    private void validateForm() {
        tilEditarMotivo.setError(null);

        String nuevoMotivo = getText(etEditarMotivo);

        if (TextUtils.isEmpty(nuevoMotivo)) {
            tilEditarMotivo.setError("El motivo de consulta no puede estar vacío");
            etEditarMotivo.requestFocus();
            return;
        }

        if (consultaId == -1) {
            Toast.makeText(
                    this,
                    "No se pudo identificar la consulta.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        String accessToken = sessionManager.getAccessToken();

        if (accessToken.isEmpty()) {
            Toast.makeText(
                    this,
                    "Sesión caducada. Inicia sesión de nuevo.",
                    Toast.LENGTH_LONG
            ).show();
            goToLogin();
            return;
        }

        updateConsulta(nuevoMotivo, accessToken);
    }

    private void updateConsulta(String nuevoMotivo, String accessToken) {
        btnGuardarCambios.setEnabled(false);
        btnGuardarCambios.setText("Guardando...");

        JSONObject body = new JSONObject();

        try {
            body.put("motivo", nuevoMotivo);
        } catch (JSONException exception) {
            resetGuardarButton();
            Toast.makeText(this, "Error preparando los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiConfig.getConsultaDetailUrl(consultaId);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                response -> {
                    resetGuardarButton();

                    String message = response.optString("message", "Consulta actualizada correctamente");
                    Toast.makeText(EditarConsultaActivity.this, message, Toast.LENGTH_LONG).show();

                    JSONObject consulta = response.optJSONObject("consulta");

                    Intent resultIntent = new Intent();

                    if (consulta != null) {
                        resultIntent.putExtra("CONSULTA_ID", consulta.optInt("id", consultaId));
                        resultIntent.putExtra("CONSULTA_MOTIVO", consulta.optString("motivo", nuevoMotivo));
                        resultIntent.putExtra("CONSULTA_ESTADO", consulta.optString("estado", "pendiente"));
                        resultIntent.putExtra("CONSULTA_CATEGORIA", consulta.optString("categoria", ""));
                        resultIntent.putExtra("CONSULTA_PRIORIDAD_IA", consulta.optInt("prioridad_ia", -1));
                        resultIntent.putExtra("CONSULTA_FECHA_CREACION", consulta.optString("fecha_creacion", ""));
                    } else {
                        resultIntent.putExtra("CONSULTA_ID", consultaId);
                        resultIntent.putExtra("CONSULTA_MOTIVO", nuevoMotivo);
                    }

                    setResult(RESULT_OK, resultIntent);
                    finish();
                },
                error -> {
                    resetGuardarButton();

                    String errorMessage = getVolleyErrorMessage(error.networkResponse);
                    Toast.makeText(EditarConsultaActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        goToLogin();
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

            return "Error al actualizar la consulta.";

        } catch (Exception exception) {
            return "Error al actualizar la consulta.";
        }
    }

    private void resetGuardarButton() {
        btnGuardarCambios.setEnabled(true);
        btnGuardarCambios.setText("Guardar cambios");
    }

    private void goToLogin() {
        sessionManager.clearSession();

        Intent intent = new Intent(EditarConsultaActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }
}