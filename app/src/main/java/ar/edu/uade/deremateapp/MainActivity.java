package ar.edu.uade.deremateapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
            finish(); // se cierra esta Activity para que el usuario no pueda verla
        }
        else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // se cierra esta Activity para que el usuario no pueda verla
        }

    }
}