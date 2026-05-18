package com.example.triaje;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.triaje.session.SessionManager;
import com.example.triaje.config.ApiConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        loadRegisteredEmailIfExists();

        findViewById(R.id.tv_register_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndLogin();
            }
        });
    }

    private void loadRegisteredEmailIfExists() {
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("REGISTERED_EMAIL")) {
            String registeredEmail = intent.getStringExtra("REGISTERED_EMAIL");

            if (registeredEmail != null && !registeredEmail.trim().isEmpty()) {
                etEmail.setText(registeredEmail);
                etPassword.requestFocus();
            }
        }
    }

    private void validateAndLogin() {
        String email = getInputText(etEmail).toLowerCase();
        String password = getInputText(etPassword);

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo electrónico inválido");
            etEmail.requestFocus();
            return;
        }

        loginUser(email, password);
    }

    private void loginUser(String email, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");

        JSONObject body = new JSONObject();

        try {
            body.put("email", email);
            body.put("password", password);
        } catch (Exception exception) {
            resetLoginButton();
            Toast.makeText(this, "Error preparando los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.LOGIN_URL,
                body,
                response -> {
                    resetLoginButton();

                    JSONObject paciente = response.optJSONObject("paciente");

                    if (paciente == null) {
                        Toast.makeText(MainActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int pacienteId = paciente.optInt("id", -1);
                    String nombreCompleto = paciente.optString("nombre_completo", "");
                    String dni = paciente.optString("dni", "");
                    String pacienteEmail = paciente.optString("email", email);

                    String accessToken = response.optString("access", "");
                    String refreshToken = response.optString("refresh", "");

                    if (accessToken.isEmpty() || refreshToken.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No se recibieron los tokens de sesión", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sessionManager.saveSession(
                            accessToken,
                            refreshToken,
                            pacienteId,
                            nombreCompleto,
                            dni,
                            pacienteEmail
                    );

                    Toast.makeText(MainActivity.this, "Login correcto", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("PACIENTE_ID", pacienteId);
                    intent.putExtra("USER_NAME", nombreCompleto);
                    intent.putExtra("USER_DNI", dni);
                    intent.putExtra("USER_EMAIL", pacienteEmail);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    resetLoginButton();

                    String errorMessage = getVolleyErrorMessage(error.networkResponse);
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(request);
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
                JSONObject errors = jsonObject.getJSONObject("errors");

                if (errors.has("email")) {
                    return errors.getString("email");
                }

                if (errors.has("password")) {
                    return errors.getString("password");
                }

                return errors.toString();
            }

            return "Error al iniciar sesión.";

        } catch (Exception exception) {
            return "Error al iniciar sesión.";
        }
    }

    private void resetLoginButton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Iniciar sesión");
    }

    private String getInputText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }
}