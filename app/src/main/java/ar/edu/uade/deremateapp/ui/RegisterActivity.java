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
    @Inject
    RegistroAPIService registroAPIService;

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

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombreRegistro.getText().toString().trim();
                String apellido = editTextApellidoRegistro.getText().toString().trim();
                String email = editTextEmailRegistro.getText().toString().trim();
                String username = editTextUsernameRegistro.getText().toString().trim();
                String password = editTextPasswordRegistro.getText().toString().trim();
                String documento = editTextDocumentoRegistro.getText().toString().trim();

                // Validaciones
                if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() ||
                        username.isEmpty() || password.isEmpty() || documento.isEmpty()) {
                    textViewRegisterError.setText("Todos los campos son obligatorios.");
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    return;
                }

                if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    textViewRegisterError.setText("El correo ingresado no es válido.");
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    return;
                }

                if (password.length() < 6) {
                    textViewRegisterError.setText("La contraseña debe tener al menos 6 caracteres.");
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    return;
                }

                if (username.contains(" ")) {
                    textViewRegisterError.setText("El nombre de usuario no puede contener espacios.");
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    return;
                }

                int documentoInt;
                try {
                    documentoInt = Integer.parseInt(documento);
                    if (documentoInt <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    textViewRegisterError.setText("El documento debe ser un número válido.");
                    textViewRegisterError.setVisibility(View.VISIBLE);
                    return;
                }

                textViewRegisterError.setVisibility(View.GONE); // Ocultamos errores si todo está bien

                UsuarioDTO usuarioDTO = new UsuarioDTO();
                usuarioDTO.setApellido(apellido);
                usuarioDTO.setEmail(email);
                usuarioDTO.setDocumento(documentoInt);
                usuarioDTO.setPassword(password);
                usuarioDTO.setNombre(nombre);
                usuarioDTO.setUsername(username);

                registerUser(usuarioDTO);
            }
        });

        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Vuelve a la LoginActivity
            }
        });
    }

    private void registerUser(UsuarioDTO usuarioDTO) {
        registroAPIService.doRegistro(usuarioDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                if (response.isSuccessful()) {
                    // Pasar email al proximo intent para usarlo con el codigo
                    Intent intent = new Intent(RegisterActivity.this, ConfirmarRegistroActivity.class);
                    intent.putExtra("email", usuarioDTO.getEmail());
                    startActivity(intent);
                    finish();
                } else {
                    textViewRegisterError.setText("Error de registro.");
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
