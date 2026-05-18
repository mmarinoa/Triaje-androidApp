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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.triaje.config.ApiConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etDni, etEmail, etPassword;
    private MaterialButton btnRegister;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        requestQueue = Volley.newRequestQueue(this);

        etName = findViewById(R.id.et_name);
        etDni = findViewById(R.id.et_dni);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndRegister();
            }
        });
    }

    private void validateAndRegister() {
        String name = getInputText(etName);
        String dni = getInputText(etDni).toUpperCase();
        String email = getInputText(etEmail).toLowerCase();
        String password = getInputText(etPassword);

        if (name.isEmpty() || dni.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarFormatoDni(dni)) {
            etDni.setError("DNI debe tener 8 números y una letra");
            etDni.requestFocus();
            return;
        }

        if (!validarEmail(email)) {
            etEmail.setError("Correo electrónico inválido");
            etEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        registerUser(name, dni, email, password);
    }

    private void registerUser(String name, String dni, String email, String password) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Registrando...");

        JSONObject body = new JSONObject();

        try {
            body.put("nombre_completo", name);
            body.put("dni", dni);
            body.put("email", email);
            body.put("password", password);
        } catch (JSONException exception) {
            resetRegisterButton();
            Toast.makeText(this, "Error preparando los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.REGISTER_URL,
                body,
                response -> {
                    resetRegisterButton();

                    String message = response.optString("message", "Registro exitoso");
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("REGISTERED_EMAIL", email);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    resetRegisterButton();

                    String errorMessage = getVolleyErrorMessage(error.networkResponse);
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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

                if (errors.has("dni")) {
                    return errors.getString("dni");
                }

                if (errors.has("nombre_completo")) {
                    return errors.getString("nombre_completo");
                }

                if (errors.has("password")) {
                    return errors.getString("password");
                }

                return errors.toString();
            }

            return "Error en el registro.";

        } catch (Exception exception) {
            return "Error en el registro.";
        }
    }

    private void resetRegisterButton() {
        btnRegister.setEnabled(true);
        btnRegister.setText("Registrar");
    }

    private String getInputText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }

    private boolean validarFormatoDni(String dni) {
        return dni.matches("\\d{8}[A-Za-z]");
    }

    private boolean validarEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}