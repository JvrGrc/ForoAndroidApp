package com.example.forodepancho.adaptador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forodepancho.R;
import com.example.forodepancho.entidad.Comentario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.List;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> implements Serializable {

    Context context;
    List<Comentario> comentarioList;

    public ComentarioAdapter(Context context, List<Comentario> comentarioList) {
        this.context = context;
        this.comentarioList = comentarioList;
    }

    public void setDatos(List<Comentario> comentarios){
        this.comentarioList = comentarios;
        notifyDataSetChanged();
    }

    public List<Comentario> getDatos(){
        return comentarioList;
    }

    @NonNull
    @Override
    public ComentarioAdapter.ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comentario_view, parent, false);
        return new ComentarioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioAdapter.ComentarioViewHolder holder, int position) {

        Comentario comentario = comentarioList.get(position);

        comprobarMod(comentario.getNickname(), holder.nickname);
        holder.nickname.setText(comentario.getNickname());
        holder.texto.setText(comentario.getTexto());
        try {
            Thread.sleep(140);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return comentarioList.size();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder{

        TextView nickname;
        TextView texto;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);

            nickname = itemView.findViewById(R.id.comentarioNickname);
            texto = itemView.findViewById(R.id.comentarioTexto);
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
