package ar.edu.uade.deremateapp;

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

public class RegisterActivity extends AppCompatActivity
{

    private EditText editTextNombreRegistro;
    private EditText editTextApellidoRegistro;
    private EditText editTextEmailRegistro;
    private EditText editTextUsernameRegistro;
    private EditText editTextPasswordRegistro;
    private EditText editTextDocumentoRegistro;
    private Button buttonRegister;
    private TextView textViewLoginLink;
    private TextView textViewRegisterError;
    private RequestQueue requestQueue;

    /*** url backend
     * Nota, por el momento se tiene que modificar con la ip privada local de cada uno, hay que modificarlo ***/
    private static final String URL_REGISTRO = BuildConfig.BACKEND_URL + "/auth/registro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextNombreRegistro = findViewById(R.id.editTextNombreRegistro);
        editTextApellidoRegistro = findViewById(R.id.editTextApellidoRegistro);
        editTextEmailRegistro = findViewById(R.id.editTextEmailRegistro);
        editTextUsernameRegistro = findViewById(R.id.editTextUsernameRegistro);
        editTextPasswordRegistro = findViewById(R.id.editTextPasswordRegistro);
        editTextDocumentoRegistro = findViewById(R.id.editTextDocumentoRegistro);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);
        textViewRegisterError = findViewById(R.id.textViewRegisterError);
        requestQueue = Volley.newRequestQueue(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombreRegistro.getText().toString().trim();
                String apellido = editTextApellidoRegistro.getText().toString().trim();
                String email = editTextEmailRegistro.getText().toString().trim();
                String username = editTextUsernameRegistro.getText().toString().trim();
                String password = editTextPasswordRegistro.getText().toString().trim();
                String documento = editTextDocumentoRegistro.getText().toString().trim();

                if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || documento.isEmpty()) {
                    textViewRegisterError.setText("Todos los campos son obligatorios.");
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    return;
                }
                registerUser(nombre, apellido, email, username, password, documento);
            }
        });

        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Vuelve a la LoginActivity
            }
        });
    }

    private void registerUser(String nombre, String apellido, String email, String username, String password, String documento) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nombre", nombre);
            jsonBody.put("apellido", apellido);
            jsonBody.put("email", email);
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("documento", Integer.parseInt(documento)); // Asegúrate de que el backend espera un entero para documento
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear la petición de registro.", Toast.LENGTH_SHORT).show();
            return;
        } catch (NumberFormatException e) {
            textViewRegisterError.setText("El documento debe ser un número.");
            textViewRegisterError.setVisibility(View.VISIBLE);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_REGISTRO, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(RegisterActivity.this, "Registro exitoso.", Toast.LENGTH_SHORT).show();
                        finish(); // Vuelve a la pantalla de inicio de sesión después del registro exitoso
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    try {
                        String errorString = new String(error.networkResponse.data);
                        JSONObject errorJson = new JSONObject(errorString);
                        // Aquí puedes intentar extraer el mensaje específico del error del backend
                        if (errorJson.has("message")) {
                            textViewRegisterError.setText(errorJson.getString("message"));
                        } else {
                            textViewRegisterError.setText("Error de registro.");
                        }
                    } catch (JSONException e) {
                        textViewRegisterError.setText("Error de registro.");
                        e.printStackTrace();
                    }
                } else {
                    textViewRegisterError.setText("Error de conexión o del servidor.");
                }
                textViewRegisterError.setVisibility(View.VISIBLE);
                // También podrías loggear el error para depuración: Log.e("RegisterActivity", "Error de registro: " + error.toString());
            }
        });

        requestQueue.add(request);
    }
}
