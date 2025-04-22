package ar.edu.uade.deremateapp;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.data.UiUtils;
import ar.edu.uade.deremateapp.data.api.RegistroAPIService;
import ar.edu.uade.deremateapp.data.api.model.ConfirmacionRegistroDTO;
import ar.edu.uade.deremateapp.data.api.model.ConfirmarRecoveryDTO;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ConfirmarRecoveryActivity extends AppCompatActivity {

    private EditText editTextCodigoConfirmacion;
    private EditText editTextNewPassword;
    private Button buttonConfirmarCodigo;

    private String email;

    @Inject
    RegistroAPIService registroAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_recovery);

        editTextCodigoConfirmacion = findViewById(R.id.editTextCodigoConfirmacion);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonConfirmarCodigo = findViewById(R.id.buttonConfirmarCodigo);

        email = getIntent().getStringExtra("email");

        buttonConfirmarCodigo.setOnClickListener(v -> {
            String email = getIntent().getStringExtra("email");
            String newPassword = editTextNewPassword.getText().toString().trim();
            String codigo = editTextCodigoConfirmacion.getText().toString().trim();

            if (codigo.isEmpty()) {
                UiUtils.showErrorSnackbar(this, "El código no puede estar vacío");
                return;
            }
            if (newPassword.isEmpty()) {
                UiUtils.showErrorSnackbar(this, "La contraseña no puede estar vacío");
                return;
            }

            ConfirmarRecoveryDTO dto = new ConfirmarRecoveryDTO(email, codigo, newPassword);
            registroAPIService.doConfirmarRecovery(dto).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ConfirmarRecoveryActivity.this, "Contraseña nueva establecida con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        UiUtils.showErrorSnackbar(ConfirmarRecoveryActivity.this, "Código inválido o expirado");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    UiUtils.showErrorSnackbar(ConfirmarRecoveryActivity.this, "Error de conexión al confirmar");
                    t.printStackTrace();
                }
            });
        });
    }
}
