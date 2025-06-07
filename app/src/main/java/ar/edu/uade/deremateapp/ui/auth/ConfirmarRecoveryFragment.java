package ar.edu.uade.deremateapp.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.R;
import ar.edu.uade.deremateapp.data.UiUtils;
import ar.edu.uade.deremateapp.data.api.RegistroAPIService;
import ar.edu.uade.deremateapp.data.api.model.ConfirmarRecoveryDTO;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ConfirmarRecoveryFragment extends Fragment {

    private static final String ARG_EMAIL = "email";

    private EditText editTextCodigoConfirmacion;
    private EditText editTextNewPassword;
    private Button buttonConfirmarCodigo;

    private String email;

    @Inject
    RegistroAPIService registroAPIService;

    public static ConfirmarRecoveryFragment newInstance(String email) {
        ConfirmarRecoveryFragment fragment = new ConfirmarRecoveryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmacion_recovery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextCodigoConfirmacion = view.findViewById(R.id.editTextCodigoConfirmacion);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        buttonConfirmarCodigo = view.findViewById(R.id.buttonConfirmarCodigo);

        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
        }

        buttonConfirmarCodigo.setOnClickListener(v -> {
            String newPassword = editTextNewPassword.getText().toString().trim();
            String codigo = editTextCodigoConfirmacion.getText().toString().trim();

            if (codigo.isEmpty()) {
                UiUtils.showErrorSnackbar(requireActivity(), "El código no puede estar vacío");
                return;
            }
            if (newPassword.isEmpty()) {
                UiUtils.showErrorSnackbar(requireActivity(), "La contraseña no puede estar vacía");
                return;
            }

            ConfirmarRecoveryDTO dto = new ConfirmarRecoveryDTO(email, codigo, newPassword);
            registroAPIService.doConfirmarRecovery(dto).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Contraseña nueva establecida con éxito", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        UiUtils.showErrorSnackbar(requireActivity(), "Código inválido o expirado");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    UiUtils.showErrorSnackbar(requireActivity(), "Error de conexión al confirmar");
                    t.printStackTrace();
                }
            });
        });
    }
}