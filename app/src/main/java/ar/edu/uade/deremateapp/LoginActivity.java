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

import java.util.List;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.data.api.EntregasAPIService;
import ar.edu.uade.deremateapp.data.api.LoginAPIService;
import ar.edu.uade.deremateapp.data.api.model.EntregasReponseDTO;
import ar.edu.uade.deremateapp.data.api.model.LoginDTO;
import ar.edu.uade.deremateapp.data.api.model.LoginResponseDTO;
import ar.edu.uade.deremateapp.data.repository.token.TokenRepository;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity
{

    private EditText editTextEmailLogin;
    private EditText editTextPasswordLogin;
    private Button buttonLogin;
    private Button buttonMakeRequest;
    private TextView textViewRegisterLink;
    private TextView textViewLoginError;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences; // uso para guardar el jwtToken de forma segura en la app

    /*** url backend
     * Nota, por el momento se tiene que modificar con la ip privada local de cada uno, hay que modificarlo ***/
    private static final String URL_LOGIN = BuildConfig.BACKEND_URL + "/auth/login";

    @Inject
    LoginAPIService loginAPIService;
    @Inject
    TokenRepository tokenRepository;
    @Inject
    EntregasAPIService entregasAPIService;


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

        buttonMakeRequest = findViewById(R.id.btnDebugMakeRequest);

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

                loginAPIService.doLogin(new LoginDTO(email, password)).enqueue(new Callback<LoginResponseDTO>() {
                    @Override
                    public void onResponse(Call<LoginResponseDTO> call, retrofit2.Response<LoginResponseDTO> response) {
                        if(response.isSuccessful()){
                            System.out.println("Writing token value to repository " + response.body().getJwtToken());
                            tokenRepository.saveToken(response.body().getJwtToken());
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                        System.out.println(t.getCause());
                    }
                });
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

        buttonMakeRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                System.out.println("Debug function called");
                entregasAPIService.obtenerMisEntregas().enqueue(new Callback<List<EntregasReponseDTO>>() {
                    @Override
                    public void onResponse(Call<List<EntregasReponseDTO>> call, retrofit2.Response<List<EntregasReponseDTO>> response) {
                        for(EntregasReponseDTO entrega: response.body()){
                            System.out.println(entrega);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<EntregasReponseDTO>> call, Throwable t) {
                        System.out.println(t.getCause());
                    }
                });
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
                // Log.e("LoginActivity", "Error de login: " + error.toString());
            }
        });

        requestQueue.add(request);
    }
}
