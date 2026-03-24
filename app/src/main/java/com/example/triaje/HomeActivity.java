package com.example.triaje;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class HomeActivity extends AppCompatActivity {

    // Variables de los campos del formulario
    private TextInputEditText etHomeName;
    private TextInputEditText etHomeDni;
    private TextInputEditText etHomeReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enganchar con el layout
        setContentView(R.layout.activity_home);

        // 1. Vincular las vistas del XML con Java
        etHomeName = findViewById(R.id.et_home_name);
        etHomeDni = findViewById(R.id.et_home_dni);
        etHomeReason = findViewById(R.id.et_home_reason);

        // 2. Recoger los datos que vienen de MainActivity
        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");
        String userDni = intent.getStringExtra("USER_DNI");

        // 3. Autocompletar los campos si existen datos
        if (userName != null && !userName.isEmpty()) {
            etHomeName.setText(userName);
        }

        if (userDni != null && !userDni.isEmpty()) {
            etHomeDni.setText(userDni);
        }
    }
}