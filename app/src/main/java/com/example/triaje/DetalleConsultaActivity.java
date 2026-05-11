package com.example.triaje;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class DetalleConsultaActivity extends AppCompatActivity {

    private TextView tvConsultaTitle;
    private TextView tvConsultaMotivo;
    private TextView tvConsultaEstado;
    private TextView tvConsultaCategoria;
    private TextView tvConsultaPrioridad;
    private TextView tvConsultaFecha;

    private MaterialButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_consulta);

        initViews();
        loadConsultaData();
        setupListeners();
    }

    private void initViews() {
        tvConsultaTitle = findViewById(R.id.tv_consulta_title);
        tvConsultaMotivo = findViewById(R.id.tv_consulta_motivo);
        tvConsultaEstado = findViewById(R.id.tv_consulta_estado);
        tvConsultaCategoria = findViewById(R.id.tv_consulta_categoria);
        tvConsultaPrioridad = findViewById(R.id.tv_consulta_prioridad);
        tvConsultaFecha = findViewById(R.id.tv_consulta_fecha);

        btnVolver = findViewById(R.id.btn_volver_home);
    }

    private void loadConsultaData() {
        int consultaId = getIntent().getIntExtra("CONSULTA_ID", -1);
        String motivo = getIntent().getStringExtra("CONSULTA_MOTIVO");
        String estado = getIntent().getStringExtra("CONSULTA_ESTADO");
        String categoria = getIntent().getStringExtra("CONSULTA_CATEGORIA");
        int prioridadIa = getIntent().getIntExtra("CONSULTA_PRIORIDAD_IA", -1);
        String fechaCreacion = getIntent().getStringExtra("CONSULTA_FECHA_CREACION");

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
        tvConsultaFecha.setText("Fecha de creación: " + fechaCreacion);
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(view -> finish());
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