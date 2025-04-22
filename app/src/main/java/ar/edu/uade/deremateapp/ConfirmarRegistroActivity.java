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
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ConfirmarRegistroActivity extends AppCompatActivity {

    private EditText editTextCodigoConfirmacion;
    private Button buttonConfirmarCodigo;

    private String email;

    @Inject
    RegistroAPIService registroAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_codigo);

        editTextCodigoConfirmacion = findViewById(R.id.editTextCodigoConfirmacion);
        buttonConfirmarCodigo = findViewById(R.id.buttonConfirmarCodigo);

        email = getIntent().getStringExtra("email");

        buttonConfirmarCodigo.setOnClickListener(v -> {
            String email = getIntent().getStringExtra("email");
            String codigo = editTextCodigoConfirmacion.getText().toString().trim();

            if (codigo.isEmpty()) {
                UiUtils.showErrorSnackbar(this, "El código no puede estar vacío");
                return;
            }

            ConfirmacionRegistroDTO dto = new ConfirmacionRegistroDTO(email, codigo);
            registroAPIService.doConfirmarRegistro(dto).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ConfirmarRegistroActivity.this, "Registro confirmado con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        UiUtils.showErrorSnackbar(ConfirmarRegistroActivity.this, "Código inválido o expirado");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    UiUtils.showErrorSnackbar(ConfirmarRegistroActivity.this, "Error de conexión al confirmar");
                    t.printStackTrace();
                }
            });
        });
    }
}