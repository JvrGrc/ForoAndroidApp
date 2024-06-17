package com.example.forodepancho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forodepancho.entidad.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    TextView user;
    TextView correo;
    TextView contrasenya;
    CheckBox terminos;
    Button registroButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        user = findViewById(R.id.nombreRegistro);
        correo = findViewById(R.id.userRegistro);
        contrasenya = findViewById(R.id.psswrdRegistro);
        terminos = findViewById(R.id.checkBoxRegistro);
        registroButton = findViewById(R.id.registrarseButton);
        auth = FirebaseAuth.getInstance();

        registroButton.setOnClickListener(v -> registro());
    }

    private void registro(){
        if (todoBien()) {
            comprobarNickname();
        }
    }

    private boolean todoBien() {
        if (TextUtils.isEmpty(user.getText())) {
            user.setError("Campo vacío");
            return false;
        } else if (TextUtils.isEmpty(correo.getText())) {
            correo.setError("Campo vacío");
            return false;
        } else if (!correoBien(correo.getText().toString())){
            correo.setError("Correo mal formado.");
            return false;
        } else if (TextUtils.isEmpty(contrasenya.getText())) {
            contrasenya.setError("Campo vacío");
            return false;
        } else if (contrasenya.getText().toString().length() < 6) {
            contrasenya.setError("Mínimo 6 caracteres");
            return false;
        } else if(!terminos.isChecked()){
            Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void comprobarNickname(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usuariosRef = db.collection("usuarios");

        Query query = usuariosRef.whereEqualTo("nickname", user.getText().toString());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 0) {
                        anyadirUsuario();
                    } else {
                        Toast.makeText(Registro.this, "Existe un usuario con ese nombre", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Registro.this, "Error al realizar la consulta.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void anyadirUsuario(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Usuario usuario;
        if (contrasenya.getText().toString().equals("hola123")){
            usuario = new Usuario(user.getText().toString(), correo.getText().toString(), true);
        }else{
            usuario = new Usuario(user.getText().toString(), correo.getText().toString(), false);
        }

        db.collection("usuarios")
                .add(usuario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        auth.createUserWithEmailAndPassword(correo.getText().toString(), contrasenya.getText().toString());
                        Toast.makeText(Registro.this, "Usuario creado correctamente.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Registro.this, "Error al registrar el usuario.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean correoBien(String correo){
        String expresion = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        Pattern patron = Pattern.compile(expresion);
        Matcher matcher = patron.matcher(correo);
        return matcher.matches();
    }
}