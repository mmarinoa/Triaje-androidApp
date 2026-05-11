package com.example.triaje;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.triaje.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DetalleConsultaActivity extends AppCompatActivity {

    private static final String CONSULTAS_BASE_URL = "http://10.0.2.2:8000/api/consultas/";

    private int consultaId = -1;

    private TextView tvConsultaTitle;
    private TextView tvConsultaMotivo;
    private TextView tvConsultaEstado;
    private TextView tvConsultaCategoria;
    private TextView tvConsultaPrioridad;
    private TextView tvConsultaFecha;

    private MaterialButton btnActualizarEstado;
    private MaterialButton btnVolver;

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_consulta);

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        initViews();
        loadConsultaDataFromIntent();
        setupListeners();
    }

    private void initViews() {
        tvConsultaTitle = findViewById(R.id.tv_consulta_title);
        tvConsultaMotivo = findViewById(R.id.tv_consulta_motivo);
        tvConsultaEstado = findViewById(R.id.tv_consulta_estado);
        tvConsultaCategoria = findViewById(R.id.tv_consulta_categoria);
        tvConsultaPrioridad = findViewById(R.id.tv_consulta_prioridad);
        tvConsultaFecha = findViewById(R.id.tv_consulta_fecha);

        btnActualizarEstado = findViewById(R.id.btn_actualizar_estado);
        btnVolver = findViewById(R.id.btn_volver_home);
    }

    private void loadConsultaDataFromIntent() {
        consultaId = getIntent().getIntExtra("CONSULTA_ID", -1);

        String motivo = getIntent().getStringExtra("CONSULTA_MOTIVO");
        String estado = getIntent().getStringExtra("CONSULTA_ESTADO");
        String categoria = getIntent().getStringExtra("CONSULTA_CATEGORIA");
        int prioridadIa = getIntent().getIntExtra("CONSULTA_PRIORIDAD_IA", -1);
        String fechaCreacion = getIntent().getStringExtra("CONSULTA_FECHA_CREACION");

        renderConsultaData(
                consultaId,
                motivo,
                estado,
                categoria,
                prioridadIa,
                fechaCreacion
        );
    }

    private void setupListeners() {
        btnActualizarEstado.setOnClickListener(view -> refreshConsulta());
        btnVolver.setOnClickListener(view -> finish());
    }

    private void refreshConsulta() {
        if (consultaId == -1) {
            Toast.makeText(this, "No se pudo identificar la consulta.", Toast.LENGTH_LONG).show();
            return;
        }

        String accessToken = sessionManager.getAccessToken();

        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Sesión caducada. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show();
            goToLogin();
            return;
        }

        btnActualizarEstado.setEnabled(false);
        btnActualizarEstado.setText("Actualizando...");

        String url = CONSULTAS_BASE_URL + consultaId + "/";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    resetActualizarButton();

                    int updatedConsultaId = response.optInt("id", consultaId);
                    String motivo = response.optString("motivo", "");
                    String estado = response.optString("estado", "pendiente");
                    String categoria = response.optString("categoria", "");
                    int prioridadIa = response.optInt("prioridad_ia", -1);
                    String fechaCreacion = response.optString("fecha_creacion", "");

                    renderConsultaData(
                            updatedConsultaId,
                            motivo,
                            estado,
                            categoria,
                            prioridadIa,
                            fechaCreacion
                    );

                    Toast.makeText(
                            DetalleConsultaActivity.this,
                            "Estado actualizado",
                            Toast.LENGTH_SHORT
                    ).show();
                },
                error -> {
                    resetActualizarButton();

                    String errorMessage = getVolleyErrorMessage(error.networkResponse);
                    Toast.makeText(DetalleConsultaActivity.this, errorMessage, Toast.LENGTH_LONG).show();

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

    private void renderConsultaData(
            int consultaId,
            String motivo,
            String estado,
            String categoria,
            int prioridadIa,
            String fechaCreacion
    ) {
        if (motivo == null || motivo.trim().isEmpty()) {
            motivo = "No disponible";
        }

        if (estado == null || estado.trim().isEmpty()) {
            estado = "pendiente";
        }

        if (categoria == null || categoria.trim().isEmpty() || categoria.equals("null")) {
            categoria = "Pendiente de clasificar";
        }

        String prioridadTexto = prioridadIa == -1
                ? "Pendiente de clasificar"
                : String.valueOf(prioridadIa);

        if (fechaCreacion == null || fechaCreacion.trim().isEmpty()) {
            fechaCreacion = "No disponible";
        }

        if (consultaId != -1) {
            tvConsultaTitle.setText("Consulta #" + consultaId);
        } else {
            tvConsultaTitle.setText("Detalle de consulta");
        }

        tvConsultaMotivo.setText(motivo);
        tvConsultaEstado.setText("Estado: " + formatEstado(estado));
        tvConsultaCategoria.setText("Categoría: " + categoria);
        tvConsultaPrioridad.setText("Prioridad IA: " + prioridadTexto);
        tvConsultaFecha.setText("Fecha de creación:\n" + fechaCreacion);
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

    private void resetActualizarButton() {
        btnActualizarEstado.setEnabled(true);
        btnActualizarEstado.setText("Actualizar estado");
    }

    private void goToLogin() {
        sessionManager.clearSession();

        Intent intent = new Intent(DetalleConsultaActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String formatEstado(String estado) {
        if (estado == null) {
            return "Pendiente";
        }

        switch (estado) {
            case "pendiente":
                return "Pendiente";
            case "en_espera":
                return "En espera";
            case "atendida":
                return "Atendida";
            case "cancelada":
                return "Cancelada";
            default:
                return estado;
        }
    }
}