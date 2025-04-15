package ar.edu.uade.deremateapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

public class LoginActivity extends AppCompatActivity
{

    private EditText editTextEmailLogin;
    private EditText editTextPasswordLogin;
    private Button buttonLogin;
    private TextView textViewRegisterLink;
    private TextView textViewLoginError;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences; // uso para guardar el jwtToken de forma segura en la app

    /*** url backend ***/
    private static final String URL_LOGIN = "http://localhost:8080/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //TODO
    }

    private void login(String email, String password)
    {
        //TODO
    }





}
