package ar.edu.uade.deremateapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.data.api.UsuarioService;
import ar.edu.uade.deremateapp.data.api.model.UsuarioDTO;
import ar.edu.uade.deremateapp.data.repository.UsuarioRepository;
import ar.edu.uade.deremateapp.data.repository.token.TokenRepository;
import ar.edu.uade.deremateapp.ui.main.HomeFragment;
import ar.edu.uade.deremateapp.ui.main.ProfileFragment;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity
{
    @Inject
    TokenRepository tokenRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    UsuarioService usuarioService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment profileFragment = new ProfileFragment();
        Fragment homeFragment = HomeFragment.newInstance();

        setCurrentFragment(profileFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.profile) {
                setCurrentFragment(profileFragment);
            }else if(item.getItemId() == R.id.home) {
                setCurrentFragment(homeFragment);
            }
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String jwtToken = tokenRepository.getToken();
        if(jwtToken == null || jwtToken.isEmpty()) // si no existe jwtToken, se procede a redirigir al usuario al AuthActivity
        {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        TextView textViewUserName = findViewById(R.id.textViewUserName);
        if (tokenRepository.hayToken()) {
            usuarioService.getUsuario().enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UsuarioDTO usuario = response.body();
                        usuarioRepository.saveUsuario(usuario);

                        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
                        textViewUserName.setText(nombreCompleto);
                    } else {
                        textViewUserName.setText("Usuario desconocido");
                    }
                }

                @Override
                public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                    UsuarioDTO cached = usuarioRepository.getUsuario();
                    if (cached != null) {
                        textViewUserName.setText(cached.getNombre() + " " + cached.getApellido());
                    } else {
                        textViewUserName.setText("Error al cargar usuario");
                    }
                }
            });
        }

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> {
            tokenRepository.clearToken();
            usuarioRepository.clearUsuario();
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}