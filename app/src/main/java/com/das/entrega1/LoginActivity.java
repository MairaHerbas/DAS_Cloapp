package com.das.entrega1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword, etEmail;
    private Button btnIniciarSesion, btnRegistrarse;
    private boolean modoRegistro = false;

    //IP DE GOOGLE CLOUD
    private final String URL_LOGIN = "http://34.130.150.158:81/login.php";
    private final String URL_REGISTRO = "http://34.130.150.158:81/registro.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        btnIniciarSesion.setOnClickListener(v -> {
            if (modoRegistro) {
                modoRegistro = false;
                etEmail.setVisibility(View.GONE);
                btnIniciarSesion.setText(getString(R.string.btn_iniciar_sesion));
            } else {
                ejecutarPeticion(URL_LOGIN);
            }
        });

        btnRegistrarse.setOnClickListener(v -> {
            if (!modoRegistro) {
                modoRegistro = true;
                etEmail.setVisibility(View.VISIBLE);
                btnIniciarSesion.setText(getString(R.string.btn_volver_login));
            } else {
                ejecutarPeticion(URL_REGISTRO);
            }
        });
    }

    private void ejecutarPeticion(String url) {
        final String usuario = etUsuario.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_rellena_campos), Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String estado = jsonObject.getString("estado");
                        String mensaje = jsonObject.getString("mensaje"); // El mensaje nos lo da el PHP

                        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();

                        if (estado.equals("ok") && url.equals(URL_LOGIN)) {
                            // 1. Abrimos la memoria de Android
                            android.content.SharedPreferences prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                            String ultimoUsuario = prefs.getString("usuario_actual", "");

                            // 2. Si el que entra es un usuario DISTINTO al anterior, vaciamos el armario local
                            if (!ultimoUsuario.equals(usuario)) {
                                getContentResolver().delete(RopaProvider.CONTENT_URI, null, null);
                            }

                            // 3. Guardamos el nombre del nuevo usuario
                            android.content.SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("usuario_actual", usuario);
                            editor.apply();

                            // Si el login es correcto, pasamos a la pantalla principal
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (estado.equals("ok") && url.equals(URL_REGISTRO)) {
                            modoRegistro = false;
                            etEmail.setVisibility(View.GONE);
                            btnIniciarSesion.setText(getString(R.string.btn_iniciar_sesion));
                        }

                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, getString(R.string.toast_error_procesando), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(LoginActivity.this, getString(R.string.toast_error_red) + error.getMessage(), Toast.LENGTH_SHORT).show())
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("username", usuario);
                parametros.put("password", password);
                if (modoRegistro) {
                    parametros.put("email", email);
                }
                return parametros;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}