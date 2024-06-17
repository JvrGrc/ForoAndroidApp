package com.example.forodepancho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.forodepancho.entidad.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class AgregarPublicacion extends AppCompatActivity {

    EditText titulo;
    EditText publicacion;
    Button publicar;
    Spinner choiceCategoria;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_publicacion);

        titulo = findViewById(R.id.tituloPublicacion);
        publicacion = findViewById(R.id.textoPublicacion);
        publicar = findViewById(R.id.buttonPublicar);
        choiceCategoria = findViewById(R.id.choiceCategoria);

        List<String> listSpinner = Arrays.asList("Libros", "Peliculas", "Deportes", "Cocina", "Herbología", "Programación", "Mecánica");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listSpinner);
        choiceCategoria.setAdapter(adapter);


        publicar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(titulo.getText())){
                    titulo.setError("Debe contener un título.");
                }else if (TextUtils.isEmpty(publicacion.getText())){
                    publicacion.setError("La publicación debe contener texto.");
                }else{
                    subirPost();
                    finish();
                }
            }
        });
    }

    private void subirPost(){
        if (TextUtils.isEmpty(titulo.getText())){
            titulo.setError("No puede estar vacío");
        } else if (TextUtils.isEmpty(publicacion.getText())){
            publicacion.setError("No puede estar vacío");
        } else{
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        QuerySnapshot document = task.getResult();
                        List<DocumentSnapshot> d = document.getDocuments();
                        for (DocumentSnapshot ds : d){
                            if (ds.get("correo").equals(user.getEmail())){
                                String nick = (String) ds.get("nickname");
                                Post post = new Post(nick, titulo.getText().toString(), choiceCategoria.getSelectedItem().toString(), publicacion.getText().toString());
                                db.collection("posts").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        String documentId = documentReference.getId();
                                        post.setId(documentId);
                                        db.collection("posts").document(documentId).set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(AgregarPublicacion.this, "Subido correctamente.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AgregarPublicacion.this, "Error al subir el post.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AgregarPublicacion.this, "Error al subir el post.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }else{
                        Toast.makeText(AgregarPublicacion.this, "Error al obtener el documento", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}