package com.example.forodepancho.adaptador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forodepancho.entidad.Post;
import com.example.forodepancho.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> implements Serializable {

    Context context;
    List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    public void setDatos(List<Post> listaDatos) {
        this.postList = listaDatos;
    }
    public List<Post> getDatos() {
        return postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.post_view, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = postList.get(position);

        comprobarMod(post.getNickname(), holder.nickname);

        holder.nickname.setText(post.getNickname());
        holder.fecha.setText(post.getCategoria());
        holder.titulo.setText(post.getTitulo());
        holder.texto.setText(post.getTexto());
        try {
            Thread.sleep(140);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        TextView nickname;
        TextView titulo;
        TextView texto;
        TextView fecha;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            nickname = itemView.findViewById(R.id.textViewNombre);
            titulo = itemView.findViewById(R.id.textViewTitulo);
            texto = itemView.findViewById(R.id.textViewInfo);
            fecha = itemView.findViewById(R.id.textViewCategoria);
        }
    }

    private void comprobarMod(String nick, TextView nickname){
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
                                nickname.setTextColor(Color.rgb(102, 0, 153));
                            }else{
                                nickname.setTextColor(Color.rgb(255, 255, 255));
                            }
                        }
                    }
                }
            }
        });
    }
}
