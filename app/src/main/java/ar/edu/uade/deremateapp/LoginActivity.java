package ar.edu.uade.deremateapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity
{

    private EditText editTextEmailLogin;
    private EditText editTextPasswordLogin;
    private Button buttonLogin;
    private TextView textViewRegisterLink;
    private TextView textViewLoginError;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences; // uso para guardar el jwtToken de forma segura en la app

    /*** url backend ***/
    private static final String URL_LOGIN = "http://localhost:8080/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmailLogin = findViewById(R.id.editTextEmailLogin);
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegisterLink = findViewById(R.id.textViewRegisterLink);
        textViewLoginError = findViewById(R.id.textViewLoginError);
        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmailLogin.getText().toString().trim();
                String password = editTextPasswordLogin.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    textViewLoginError.setText("Por favor, introduce tu email y contraseña.");
                    textViewLoginError.setVisibility(View.VISIBLE);
                    return;
                }
                login(email, password);
            }
        });

        // Este es el OnClickListener que faltaba para el enlace de registro
        textViewRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String email, String password) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear la petición.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_LOGIN, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String jwtToken = response.getString("jwtToken");
                            // Guardar el token JWT
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("jwtToken", jwtToken);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                            // Redirige a la siguiente Activity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Cierra la actividad de login para que el usuario no pueda volver atrás
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textViewLoginError.setText("Error al procesar la respuesta del servidor.");
                            textViewLoginError.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewLoginError.setText("Error de autenticación. Credenciales incorrectas.");
                textViewLoginError.setVisibility(View.VISIBLE);
                // También podrías loggear el error para depuración: Log.e("LoginActivity", "Error de login: " + error.toString());
            }
        });

        requestQueue.add(request);
    }
}
