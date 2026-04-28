package com.example.triaje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        pacienteId = intent.getIntExtra("PACIENTE_ID", -1);
        userName = intent.getStringExtra("USER_NAME");
        userDni = intent.getStringExtra("USER_DNI");
        userEmail = intent.getStringExtra("USER_EMAIL");

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

        Toast.makeText(this, "Pantalla preparada. Falta conectar envío de consulta.", Toast.LENGTH_SHORT).show();

        /*
         * Siguiente paso:
         * Aquí llamaremos a Django para crear la consulta real.
         */
    }

    private void logout() {
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