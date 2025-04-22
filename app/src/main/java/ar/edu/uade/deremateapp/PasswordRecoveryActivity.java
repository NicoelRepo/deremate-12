package ar.edu.uade.deremateapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.data.api.RegistroAPIService;
import ar.edu.uade.deremateapp.data.api.model.UsuarioDTO;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@AndroidEntryPoint
public class PasswordRecoveryActivity extends AppCompatActivity {



        private EditText editTextEmailRecovery;
        private Button buttonRecovery;
        private TextView textViewLoginLink;
        private TextView textViewRegisterError;
        @Inject
        RegistroAPIService registroAPIService;

        /*** url backend
         * Nota, por el momento se tiene que modificar con la ip privada local de cada uno, hay que modificarlo ***/
        private static final String URL_REGISTRO = BuildConfig.BACKEND_URL + "/auth/registro";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        editTextEmailRecovery = findViewById(R.id.editTextEmailRecoveey);
        buttonRecovery = findViewById(R.id.buttonPasswordRecovey);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);
        textViewRegisterError = findViewById(R.id.textViewRegisterError);

            buttonRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmailRecovery.getText().toString().trim();

                // Validaciones
                if (email.isEmpty()) {
                    textViewRegisterError.setText("Todos los campos son obligatorios.");
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    return;
                }

                if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    textViewRegisterError.setText("El correo ingresado no es válido.");
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    return;
                }


                textViewRegisterError.setVisibility(View.GONE); // Ocultamos errores si todo está bien

                UsuarioDTO usuarioDTO = new UsuarioDTO();
                usuarioDTO.setEmail(email);

                recoverUser(usuarioDTO);
            }
        });

        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Vuelve a la LoginActivity
            }
        });
    }

        private void recoverUser(UsuarioDTO usuarioDTO) {
        registroAPIService.doRecovery(usuarioDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                if (response.isSuccessful()) {
                    // Pasar email al proximo intent para usarlo con el codigo
                    Intent intent = new Intent(PasswordRecoveryActivity.this, ConfirmarRecoveryActivity.class);
                    intent.putExtra("email", usuarioDTO.getEmail());
                    startActivity(intent);
                    finish();
                } else {
                    textViewRegisterError.setText("Error de recupero.");
                    textViewRegisterError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                textViewRegisterError.setText("Error de conexión.");
                textViewRegisterError.setVisibility(View.VISIBLE);
            }
        });
    }


}
