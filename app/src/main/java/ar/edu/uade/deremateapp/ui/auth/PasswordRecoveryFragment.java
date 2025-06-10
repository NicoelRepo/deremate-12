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
public class PasswordRecoveryFragment extends Fragment {

    private EditText editTextEmailRecovery;
    private Button buttonRecovery;
    private TextView textViewLoginLink;
    private TextView textViewRegisterError;

    @Inject
    RegistroAPIService registroAPIService;

    private static final String URL_REGISTRO = BuildConfig.BACKEND_URL + "/auth/registro";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password_recovery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextEmailRecovery = view.findViewById(R.id.editTextEmailRecoveey);
        buttonRecovery = view.findViewById(R.id.buttonPasswordRecovey);
        textViewLoginLink = view.findViewById(R.id.textViewLoginLink);
        textViewRegisterError = view.findViewById(R.id.textViewRegisterError);

        buttonRecovery.setOnClickListener(v -> {
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

            textViewRegisterError.setVisibility(View.GONE);

            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setEmail(email);

            recoverUser(usuarioDTO);
        });

        textViewLoginLink.setOnClickListener(v -> {
            // Navega de regreso al LoginFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void recoverUser(UsuarioDTO usuarioDTO) {
        registroAPIService.doRecovery(usuarioDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                if (response.isSuccessful()) {
                    // Pasar email al próximo fragmento para usarlo con el código
                    ConfirmarRecoveryFragment fragment = ConfirmarRecoveryFragment.newInstance(usuarioDTO.getEmail());
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
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