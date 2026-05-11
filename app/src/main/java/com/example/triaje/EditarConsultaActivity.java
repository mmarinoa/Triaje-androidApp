package com.example.triaje;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditarConsultaActivity extends AppCompatActivity {

    private int consultaId = -1;

    private TextInputLayout tilEditarMotivo;
    private TextInputEditText etEditarMotivo;

    private MaterialButton btnGuardarCambios;
    private MaterialButton btnCancelarEdicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_consulta);

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

        Toast.makeText(
                this,
                "Pantalla preparada. Falta conectar guardado.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }
}