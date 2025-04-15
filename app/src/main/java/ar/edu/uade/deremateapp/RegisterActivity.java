package ar.edu.uade.deremateapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

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
    private RequestQueue requestQueue;

    /*** url backend ***/
    private static final String URL_LOGIN = "http://localhost:8080/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //TODO
    }

    private void registerUser(String nombre, String apellido, String email, String username, String password, String documento)
    {
        //TODO
    }


}
