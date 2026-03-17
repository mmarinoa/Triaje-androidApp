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
                String dni = etDni.getText().toString();
                String name = etName.getText().toString();
                String pass = etPassword.getText().toString();

                // Validación básica (ejemplo: que no estén vacíos)
                if (!dni.isEmpty() && !pass.isEmpty()) {
                    // --- AQUÍ OCURRE LA MAGIA DEL SALTO ---
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    // Opcional: Pasar el nombre a la siguiente pantalla
                    intent.putExtra("USER_NAME", name);
                    startActivity(intent);
                    // Cerrar la pantalla de login para que no se pueda volver atrás con el botón del móvil
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Por favor, rellena los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}