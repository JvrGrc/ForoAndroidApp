
package com.example.forodepancho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forodepancho.adaptador.PostAdapter;
import com.example.forodepancho.adaptador.RecyclerItemClickListener;
import com.example.forodepancho.entidad.Comentario;
import com.example.forodepancho.entidad.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PantallaPrincipal extends AppCompatActivity {
    RecyclerView recycler;
    FirebaseFirestore db;
    PostAdapter adapter;
    FloatingActionButton agregar;
    List<Post> postList;
    TextView nada;
    Button logout;
    Spinner spinner;
    TextView nombre;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        recycler = findViewById(R.id.recycler);
        agregar = findViewById(R.id.buttonAgregar);
        nada = findViewById(R.id.textNada);
        logout = findViewById(R.id.buttonLogout);
        spinner = findViewById(R.id.spinnerMain);
        nombre = findViewById(R.id.nombreUser);

        List<String> listSpinner1 = Arrays.asList("Todos","Libros", "Peliculas", "Deportes", "Cocina", "Herbología", "Programación", "Mecánica");
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listSpinner1);
        spinner.setAdapter(adapterSpinner);

        db = FirebaseFirestore.getInstance();
        recycler.setHasFixedSize(true);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        adapter = new PostAdapter(this, postList);
        recycler.setAdapter(adapter);

        rellenarRecycler(spinner.getSelectedItem().toString());
        rellenarNombre();

        recycler.addOnItemTouchListener(new RecyclerItemClickListener(this, recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int posicion) {
                Intent intent = new Intent(v.getContext(), Detalles.class);
                intent.putExtra("post", adapter.getDatos().get(posicion));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View v, int posicion) {
                comprobarMod(posicion, spinner.getSelectedItem().toString());
            }
        }));


        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), AgregarPublicacion.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogo = new AlertDialog.Builder(PantallaPrincipal.this).setPositiveButton(
                        "Salir de mi cuenta", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                System.out.println(user);
                                System.out.println(user.getEmail());
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("Confirmar")
                        .setMessage("¿Desea salir de su cuenta?")
                        .create();
                dialogo.show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rellenarRecycler(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void rellenarRecycler(String categoria){
        if (categoria.equals("Todos")) {
            db.collection("posts").addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(PantallaPrincipal.this, "Error al obtener los datos.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    adapter.setDatos(value.toObjects(Post.class));
                    adapter.notifyDataSetChanged();
                    if (adapter.getDatos().isEmpty()) {
                        nada.setVisibility(View.VISIBLE);
                    } else {
                        nada.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }else{
            db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        QuerySnapshot document = task.getResult();
                        List<DocumentSnapshot> d = document.getDocuments();
                        List<Post> postList = new ArrayList<>();
                        for (DocumentSnapshot ds : d){
                            if (ds.get("categoria").equals(categoria)){
                                Post p = new Post(ds.get("nickname").toString(), (String) ds.get("titulo"), (String) ds.get("categoria"), (String) ds.get("texto"), (ArrayList<Comentario>) ds.get("comentarios"), (String) ds.get("id"));
                                postList.add(p);
                            }
                        }
                        adapter.setDatos(postList);
                        adapter.notifyDataSetChanged();
                        if (adapter.getDatos().isEmpty()){
                            nada.setVisibility(View.VISIBLE);
                        }else{
                            nada.setVisibility(View.INVISIBLE);
                        }
                    }else{
                        Toast.makeText(PantallaPrincipal.this, "Error al obtener el documento", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void comprobarMod(int posicion, String categoria) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Post p = adapter.getDatos().get(posicion);
        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot document = task.getResult();
                    List<DocumentSnapshot> d = document.getDocuments();
                    for (DocumentSnapshot ds : d) {
                        if (ds.get("correo").equals(user.getEmail())) {
                            if (ds.get("mod").equals(true) || ds.get("nickname").equals(p.getNickname())){
                                AlertDialog dialogo = new AlertDialog.Builder(PantallaPrincipal.this).setPositiveButton(
                                                "Confirmar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        borrar(posicion, categoria);
                                                    }
                                                })
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setTitle("Confirmar")
                                        .setMessage("¿Deseas borrar este post?")
                                        .create();
                                dialogo.show();
                            }else{
                                Toast.makeText(PantallaPrincipal.this, "No tienes permisos para borrar posts.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }else{
                    Toast.makeText(PantallaPrincipal.this, "Error con el documento.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void borrar(int posicion, String categoria){
        Post p = adapter.getDatos().get(posicion);
        DocumentReference dr = db.collection("posts").document(p.getId());
        dr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PantallaPrincipal.this, "Borrado correctamente", Toast.LENGTH_SHORT).show();
                rellenarRecycler(categoria);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PantallaPrincipal.this, "No se ha podido borrar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rellenarNombre(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot document = task.getResult();
                    List<DocumentSnapshot> d = document.getDocuments();
                    for (DocumentSnapshot ds : d) {
                        if (ds.get("correo").equals(user.getEmail())) {
                            if (ds.get("mod").equals(true)){
                                nombre.setText(ds.get("nickname").toString());
                                nombre.setTextColor(Color.rgb(102, 0, 153));
                            }else{
                                nombre.setText(ds.get("nickname").toString());
                                nombre.setTextColor(Color.rgb(255, 255, 255));
                            }
                        }
                    }
                }else{
                    Toast.makeText(PantallaPrincipal.this, "Error con el documento.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}