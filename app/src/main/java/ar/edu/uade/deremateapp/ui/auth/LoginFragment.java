package ar.edu.uade.deremateapp.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.BuildConfig;
import ar.edu.uade.deremateapp.MainActivity;
import ar.edu.uade.deremateapp.R;
import ar.edu.uade.deremateapp.data.UiUtils;
import ar.edu.uade.deremateapp.data.api.EntregasAPIService;
import ar.edu.uade.deremateapp.data.api.LoginAPIService;
import ar.edu.uade.deremateapp.data.api.model.LoginDTO;
import ar.edu.uade.deremateapp.data.api.model.LoginResponseDTO;
import ar.edu.uade.deremateapp.data.repository.token.TokenRepository;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

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


    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_login, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextEmailLogin = view.findViewById(R.id.editTextEmailLogin);
        editTextPasswordLogin = view.findViewById(R.id.editTextPasswordLogin);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        textViewRegisterLink = view.findViewById(R.id.textViewRegisterLink);
        textViewPasswordRecovery = view.findViewById(R.id.textPasswordRecovery);
        textViewLoginError = view.findViewById(R.id.textViewLoginError);
        requestQueue = Volley.newRequestQueue(view.getContext());
        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);


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

                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        UiUtils.showErrorSnackbar(requireActivity(), "Las credenciales no son válidas");
                    }
                }

                @Override
                public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                    UiUtils.showErrorSnackbar(requireActivity(), "Ha ocurrido un error al iniciar sesión: " + t.getMessage());
                }
            });
        });

        // Navegación a RegisterFragment (si ya migraste RegisterActivity a fragment, usa esto)
        textViewRegisterLink.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

// Navegación a PasswordRecoveryFragment
        textViewPasswordRecovery.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new PasswordRecoveryFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}
