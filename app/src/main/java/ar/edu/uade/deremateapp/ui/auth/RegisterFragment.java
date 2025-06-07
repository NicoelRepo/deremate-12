package ar.edu.uade.deremateapp.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.BuildConfig;
import ar.edu.uade.deremateapp.R;
import ar.edu.uade.deremateapp.data.api.RegistroAPIService;
import ar.edu.uade.deremateapp.data.api.model.UsuarioDTO;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

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

    private static final String URL_REGISTRO = BuildConfig.BACKEND_URL + "/auth/registro";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextNombreRegistro = view.findViewById(R.id.editTextNombreRegistro);
        editTextApellidoRegistro = view.findViewById(R.id.editTextApellidoRegistro);
        editTextEmailRegistro = view.findViewById(R.id.editTextEmailRegistro);
        editTextUsernameRegistro = view.findViewById(R.id.editTextUsernameRegistro);
        editTextPasswordRegistro = view.findViewById(R.id.editTextPasswordRegistro);
        editTextDocumentoRegistro = view.findViewById(R.id.editTextDocumentoRegistro);
        buttonRegister = view.findViewById(R.id.buttonRegister);
        textViewLoginLink = view.findViewById(R.id.textViewLoginLink);
        textViewRegisterError = view.findViewById(R.id.textViewRegisterError);

        buttonRegister.setOnClickListener(v -> {
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
        });

        textViewLoginLink.setOnClickListener(v -> {
            // Navegación de regreso al LoginFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void registerUser(UsuarioDTO usuarioDTO) {
        registroAPIService.doRegistro(usuarioDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                if (response.isSuccessful()) {
                    // Navega al fragmento de confirmación de registro, pasando el email
                    ConfirmarRegistroFragment fragment = ConfirmarRegistroFragment.newInstance(usuarioDTO.getEmail());
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
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