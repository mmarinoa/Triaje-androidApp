package com.example.triaje;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    // 1. Declarar las variables de la interfaz
    private TextInputEditText etDni, etEmail, etPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 2. Vincular con el XML mediante los IDs
        etDni = findViewById(R.id.et_dni);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        // 3. Configurar el evento del botón
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capturar el texto de los inputs
                String dni = etDni.getText().toString();
                String email = etEmail.getText().toString();
                String pass = etPassword.getText().toString();

                // Ejemplo rápido: mostrar un mensaje con el DNI
                if (!dni.isEmpty()) {
                    Toast.makeText(MainActivity.this, "DNI: " + dni, Toast.LENGTH_SHORT).show();
                } else {
                    etDni.setError("Campo obligatorio");
                }
            }
        });

    }
}