package com.example.triaje;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.triaje.session.SessionManager;
import com.example.triaje.config.ApiConfig;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DetalleConsultaActivity extends AppCompatActivity {

    private int consultaId = -1;

    private TextView tvConsultaTitle;
    private TextView tvConsultaMotivo;
    private TextView tvConsultaEstado;
    private TextView tvConsultaCategoria;
    private TextView tvConsultaPrioridad;
    private TextView tvConsultaFecha;

    private MaterialButton btnActualizarEstado;
    private MaterialButton btnVolver;

    private String motivoActual = "";
    private MaterialButton btnModificarConsulta;
    private MaterialButton btnCancelarConsulta;
    private RequestQueue requestQueue;
    private SessionManager sessionManager;
    private ActivityResultLauncher<Intent> editarConsultaLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_consulta);

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        setupEditarConsultaLauncher();

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
        btnCancelarConsulta = findViewById(R.id.btn_cancelar_consulta);
        btnModificarConsulta = findViewById(R.id.btn_modificar_consulta);
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
        btnModificarConsulta.setOnClickListener(view -> openEditarConsulta());
        btnCancelarConsulta.setOnClickListener(view -> showCancelConfirmation());
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

        String url = ApiConfig.getConsultaDetailUrl(consultaId);

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

        motivoActual = motivo;

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

        if ("cancelada".equals(estado)) {
            btnModificarConsulta.setEnabled(false);
            btnCancelarConsulta.setEnabled(false);
            btnCancelarConsulta.setText("Consulta cancelada");
        } else {
            btnModificarConsulta.setEnabled(true);
            btnCancelarConsulta.setEnabled(true);
            btnCancelarConsulta.setText("Cancelar consulta");
        }
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

    private void showCancelConfirmation() {
        if (consultaId == -1) {
            Toast.makeText(this, "No se pudo identificar la consulta.", Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Cancelar consulta")
                .setMessage("¿Seguro que quieres cancelar esta consulta?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> cancelConsulta())
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelConsulta() {
        String accessToken = sessionManager.getAccessToken();

        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Sesión caducada. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show();
            goToLogin();
            return;
        }

        btnCancelarConsulta.setEnabled(false);
        btnCancelarConsulta.setText("Cancelando...");

        String url = ApiConfig.getConsultaDetailUrl(consultaId);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    resetCancelarButton();

                    String message = response.optString("message", "Consulta cancelada correctamente");
                    Toast.makeText(DetalleConsultaActivity.this, message, Toast.LENGTH_LONG).show();

                    JSONObject consulta = response.optJSONObject("consulta");

                    if (consulta != null) {
                        int updatedConsultaId = consulta.optInt("id", consultaId);
                        String motivo = consulta.optString("motivo", motivoActual);
                        String estado = consulta.optString("estado", "cancelada");
                        String categoria = consulta.optString("categoria", "");
                        int prioridadIa = consulta.optInt("prioridad_ia", -1);
                        String fechaCreacion = consulta.optString("fecha_creacion", "");

                        consultaId = updatedConsultaId;

                        renderConsultaData(
                                updatedConsultaId,
                                motivo,
                                estado,
                                categoria,
                                prioridadIa,
                                fechaCreacion
                        );
                    } else {
                        tvConsultaEstado.setText("Estado: Cancelada");
                    }

                    btnModificarConsulta.setEnabled(false);
                    btnCancelarConsulta.setEnabled(false);
                    btnCancelarConsulta.setText("Consulta cancelada");
                },
                error -> {
                    resetCancelarButton();

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

    private void resetCancelarButton() {
        btnCancelarConsulta.setEnabled(true);
        btnCancelarConsulta.setText("Cancelar consulta");
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

    private void openEditarConsulta() {
        if (consultaId == -1) {
            Toast.makeText(this, "No se pudo identificar la consulta.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(DetalleConsultaActivity.this, EditarConsultaActivity.class);
        intent.putExtra("CONSULTA_ID", consultaId);
        intent.putExtra("CONSULTA_MOTIVO", motivoActual);
        editarConsultaLauncher.launch(intent);
    }

    private void setupEditarConsultaLauncher() {
        editarConsultaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                        return;
                    }

                    Intent data = result.getData();

                    int updatedConsultaId = data.getIntExtra("CONSULTA_ID", consultaId);
                    String motivo = data.getStringExtra("CONSULTA_MOTIVO");
                    String estado = data.getStringExtra("CONSULTA_ESTADO");
                    String categoria = data.getStringExtra("CONSULTA_CATEGORIA");
                    int prioridadIa = data.getIntExtra("CONSULTA_PRIORIDAD_IA", -1);
                    String fechaCreacion = data.getStringExtra("CONSULTA_FECHA_CREACION");

                    consultaId = updatedConsultaId;

                    renderConsultaData(
                            updatedConsultaId,
                            motivo,
                            estado,
                            categoria,
                            prioridadIa,
                            fechaCreacion
                    );
                }
        );
    }
}