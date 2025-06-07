package ar.edu.uade.deremateapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.data.UiUtils;
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
public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmailLogin;
    private EditText editTextPasswordLogin;
    private Button buttonLogin;
    private TextView textViewRegisterLink;
    private TextView textViewPasswordRecovery;
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
        textViewPasswordRecovery = findViewById(R.id.textPasswordRecovery);
        textViewLoginError = findViewById(R.id.textViewLoginError);
        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);


        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmailLogin.getText().toString().trim();
            String password = editTextPasswordLogin.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                textViewLoginError.setText("Por favor, introduce tu email y contraseña.");
                textViewLoginError.setVisibility(View.VISIBLE);
                return;
            }

            loginAPIService.doLogin(new LoginDTO(email, password)).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<LoginResponseDTO> call, retrofit2.Response<LoginResponseDTO> response) {
                    if (response.isSuccessful()) {
                        System.out.println("Writing token value to repository " + response.body().getJwtToken());
                        tokenRepository.saveToken(response.body().getJwtToken());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        UiUtils.showErrorSnackbar(LoginActivity.this, "Las credenciales no son válidas");
                    }
                }

                @Override
                public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                    UiUtils.showErrorSnackbar(LoginActivity.this, "Ha ocurrido un error al iniciar sesión: " + t.getMessage());
                }
            });
        });

        // Este es el OnClickListener que faltaba para el enlace de registro
        textViewRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        textViewPasswordRecovery.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
            startActivity(intent);
        });
    }
}
