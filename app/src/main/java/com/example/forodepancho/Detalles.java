package com.example.forodepancho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forodepancho.adaptador.ComentarioAdapter;
import com.example.forodepancho.adaptador.RecyclerItemClickListener;
import com.example.forodepancho.entidad.Comentario;
import com.example.forodepancho.entidad.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detalles extends AppCompatActivity {

    TextView nickname;
    TextView categoria;
    TextView titulo;
    TextView texto;
    TextView nada;
    EditText comentario;
    Button buttonComentario;
    RecyclerView recyclerComentarios;
    FirebaseFirestore db;
    ComentarioAdapter adapter;
    List<Comentario> comentariosList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra("post");

        nickname = findViewById(R.id.detallesNickname);
        categoria = findViewById(R.id.detallesCategoria);
        titulo = findViewById(R.id.detallesTitulo);
        texto = findViewById(R.id.detallesTexto);
        recyclerComentarios = findViewById(R.id.recyclerComentarios);
        nada = findViewById(R.id.detallesNada);
        comentario = findViewById(R.id.comentario);
        buttonComentario = findViewById(R.id.buttonComentario);

        comprobarMod(post.getNickname());
        categoria.setText(post.getCategoria());
        titulo.setText(post.getTitulo());
        texto.setText(post.getTexto());

        db = FirebaseFirestore.getInstance();
        recyclerComentarios.setHasFixedSize(true);

        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));

        comentariosList = new ArrayList<>();
        adapter = new ComentarioAdapter(this, comentariosList);
        recyclerComentarios.setAdapter(adapter);

        rellenarRecycler(post.getId());

        buttonComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comentar(post.getId(), v);
            }
        });

        recyclerComentarios.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerComentarios, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int posicion) {
                Intent intent = new Intent(v.getContext(), Detalles.class);
                intent.putExtra("post", adapter.getDatos().get(posicion));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View v, int posicion) {
                comprobarBorrado(post.getNickname().toString(),posicion, post.getId());
            }
        }));


    }

    private void rellenarRecycler(String id){

        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot document = task.getResult();
                    List<DocumentSnapshot> d = document.getDocuments();
                    List<Comentario> comentarios = new ArrayList<>();
                    for (DocumentSnapshot ds : d){
                        if (ds.get("id").equals(id)){
                            ArrayList<HashMap<String, String>> total = (ArrayList<HashMap<String, String>>) ds.get("comentarios");
                            for (HashMap<String, String> hash : total){
                                Comentario c = new Comentario(hash.get("nickname"), hash.get("texto"));
                                comentarios.add(c);
                            }
                        }
                    }

                    adapter.setDatos(comentarios);
                    adapter.notifyDataSetChanged();
                    if (adapter.getDatos().isEmpty()){
                        nada.setVisibility(View.VISIBLE);
                    }else{
                        nada.setVisibility(View.INVISIBLE);
                    }
                }else{
                    Toast.makeText(Detalles.this, "Error al obtener el documento", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void comentar(String id, View v){
        if (TextUtils.isEmpty(comentario.getText())){
            comentario.setError("No puede estar vacío.");
        }else{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        QuerySnapshot document = task.getResult();
                        List<DocumentSnapshot> d = document.getDocuments();
                        for (DocumentSnapshot ds : d){
                            if (ds.get("correo").equals(user.getEmail())) {
                                String nick = (String) ds.get("nickname");
                                Comentario c = new Comentario(nick, comentario.getText().toString());
                                db.collection("posts").document(id).update("comentarios", FieldValue.arrayUnion(c))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                rellenarRecycler(id);
                                                comentario.setText("");
                                                Toast.makeText(Detalles.this, "Comentario publicado", Toast.LENGTH_SHORT).show();
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Detalles.this, "Error al agregar el comentario", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }else{
                        Toast.makeText(Detalles.this, "Error al obtener el documento", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void comprobarMod(String nick){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot document = task.getResult();
                    List<DocumentSnapshot> d = document.getDocuments();
                    for (DocumentSnapshot ds : d){
                        if (ds.get("nickname").equals(nick)) {
                            if (ds.get("mod").equals(true)) {
                                nickname.setText(nick);
                                nickname.setTextColor(Color.rgb(102, 0, 153));
                            }else{
                                nickname.setText(nick);
                            }
                        }
                    }
                }
            }
        });
    }

    private void comprobarBorrado(String nick, int posicion, String id){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot document = task.getResult();
                    List<DocumentSnapshot> d = document.getDocuments();
                    for (DocumentSnapshot ds : d){
                        if (ds.get("correo").equals(user.getEmail())) {
                            if (ds.get("mod").equals(true) || ds.get("nickname").equals(nick)) {
                                AlertDialog dialogo = new AlertDialog.Builder(Detalles.this).setPositiveButton(
                                                "Confirmar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        borrar(posicion, id);
                                                    }
                                                })
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setTitle("Confirmar")
                                        .setMessage("¿Deseas borrar este comentario?")
                                        .create();
                                dialogo.show();
                            } else {
                                Toast.makeText(Detalles.this, "No tienes permisos para borrar comentarios.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });
    }

    private void borrar(int posicion, String id){
        Comentario p = adapter.getDatos().get(posicion);
        DocumentReference dr = db.collection("posts").document(id);
        Map<String, Object> updates = new HashMap<>();
        Comentario c = adapter.getDatos().get(posicion);
        updates.put("comentarios", FieldValue.arrayRemove(c));
        dr.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Detalles.this, "Borrado correctamente", Toast.LENGTH_SHORT).show();
                        rellenarRecycler(id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Detalles.this, "No se ha podido eliminar el comentario", Toast.LENGTH_SHORT).show();
                    }
                });

        /*dr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Detalles.this, "Borrado correctamente", Toast.LENGTH_SHORT).show();
                rellenarRecycler(id);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Detalles.this, "No se ha podido borrar", Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}