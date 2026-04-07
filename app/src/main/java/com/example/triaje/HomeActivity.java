package com.example.triaje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class HomeActivity extends AppCompatActivity {

    private TextInputEditText etHomeName;
    private TextInputEditText etHomeDni;
    private TextInputEditText etHomeReason;
    private MaterialButton btnSendDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        etHomeName = findViewById(R.id.et_home_name);
        etHomeDni = findViewById(R.id.et_home_dni);
        etHomeReason = findViewById(R.id.et_home_reason);
        btnSendDoctor = findViewById(R.id.btn_send_doctor);
    }

    private void loadUserData() {
        Intent intent = getIntent();

        String userName = intent.getStringExtra("USER_NAME");
        String userDni = intent.getStringExtra("USER_DNI");

        if (userName != null && !userName.isEmpty()) {
            etHomeName.setText(userName);
        }

        if (userDni != null && !userDni.isEmpty()) {
            etHomeDni.setText(userDni);
        }
    }

    private void setupListeners() {
        btnSendDoctor.setOnClickListener(v -> validateAndSend());
    }

    private void validateAndSend() {
        clearErrors();

        String name = getText(etHomeName);
        String dni = getText(etHomeDni);
        String reason = getText(etHomeReason);

        if (TextUtils.isEmpty(name)) {
            etHomeName.setError("Introduce tu nombre completo");
            etHomeName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dni)) {
            etHomeDni.setError("Introduce tu DNI / NIE");
            etHomeDni.requestFocus();
            return;
        }

        if (!validarFormatoDNI(dni)) {
            etHomeDni.setError("El DNI debe tener 8 números y una letra");
            etHomeDni.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(reason)) {
            etHomeReason.setError("Introduce el motivo de la consulta");
            etHomeReason.requestFocus();
            return;
        }

        Toast.makeText(this, "Consulta enviada correctamente", Toast.LENGTH_SHORT).show();

        // Aquí iría después tu envío al webhook de n8n
        // enviarConsulta(name, dni, reason);
    }

    private void clearErrors() {
        etHomeName.setError(null);
        etHomeDni.setError(null);
        etHomeReason.setError(null);
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private boolean validarFormatoDNI(String dni) {
        String regexDNI = "^[0-9]{8}[A-Za-z]$";
        return dni.matches(regexDNI);
    }

}