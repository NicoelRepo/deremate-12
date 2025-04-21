package ar.edu.uade.deremateapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

import ar.edu.uade.deremateapp.data.repository.token.TokenRepository;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity
{
    @Inject
    TokenRepository tokenRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);


        Fragment profileFragment = new ProfileFragment();
        Fragment homeFragment = new HomeFragment();
        Fragment debugFragment = new DebugFragment();

        setCurrentFragment(homeFragment);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.profile) {
                setCurrentFragment(profileFragment);
            }else if(item.getItemId() == R.id.home) {
                setCurrentFragment(homeFragment);
            }else if(item.getItemId() == R.id.debug) {
                setCurrentFragment(debugFragment);
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
        if(jwtToken == null || jwtToken.isEmpty()) // si no existe jwtToken, se procede a redirigir al usuario al LoginActivity
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}