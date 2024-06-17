package com.example.forodepancho;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView user;
    TextView password;
    FirebaseAuth auth;
    Button iniciarSesion;
    Button registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializar();

        user = findViewById(R.id.user);
        password = findViewById(R.id.psswrd);
        iniciarSesion = findViewById(R.id.iniciarSesionButton);
        registro = findViewById(R.id.registroButton);

        auth = FirebaseAuth.getInstance();
        iniciarSesion.setOnClickListener(v -> inicioSesion());
        registro.setOnClickListener(v -> registro());



    }

    private void inicioSesion() {
        if (TextUtils.isEmpty(user.getText()) || TextUtils.isEmpty(password.getText())){
            Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show();
        }
        if (password.getText().toString().length() < 6){
            password.setError("Contraseña corta. Min: 6 caracteres");
        }else {
            auth.signInWithEmailAndPassword(user.getText().toString(), password.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()){
                    user.setText("");
                    password.setText("");
                    Intent intent = new Intent(getApplication(), PantallaPrincipal.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "Error con el usuario o la contraseña", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void registro(){
        user.setText("");
        password.setText("");
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
    }

    private void inicializar(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent intent = new Intent(getApplication(), PantallaPrincipal.class);
            startActivity(intent);
            finish();
        }
    }
}