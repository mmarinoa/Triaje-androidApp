package com.example.triaje;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    // 1. Declarar las variables de la interfaz
    private TextInputEditText etDni, etName, etPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //engancharlo con su layout
        setContentView(R.layout.activity_main);

        // 2. Vincular con el XML mediante los IDs
        etDni = findViewById(R.id.et_dni);
        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        // 3. Configurar el evento del botón
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Capturar el texto de los inputs
                String dni = etDni.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                // 1. Validación de campos vacíos
                if (dni.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                    return; // Detiene la ejecución
                }

                // 2. Validación específica de formato DNI (8 números + 1 letra)
                if (!validarFormatoDNI(dni)) {
                    etDni.setError("El DNI debe tener 8 números y una letra (ej: 12345678Z)");
                    return;
                }

                // 3. Validación de contraseña (ejemplo: mínimo 6 caracteres)
                if (pass.length() < 6) {
                    etPassword.setError("La contraseña debe tener al menos 6 caracteres");
                    return;
                }

                // si cumple esto se pasa a la siguiente pantalla
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("USER_DNI", dni);
                intent.putExtra("USER_NAME", name);
                startActivity(intent);
                finish();

            }
        });

    }

    // Metodo auxiliar para validar el DNI con Regex
    private boolean validarFormatoDNI(String dni) {
        // Explicación del Regex:
        // ^[0-9]{8} -> Empieza con exactamente 8 números
        // [A-Za-z]$ -> Termina con una letra (mayúscula o minúscula)
        String regexDNI = "^[0-9]{8}[A-Za-z]$";
        return dni.matches(regexDNI);
    }
}