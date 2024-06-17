package com.example.forodepancho.entidad;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Post implements Serializable {
    private String id;
    private String nickname;
    private String titulo;
    private String categoria;
    private String texto;
    private ArrayList<Comentario> comentarios;

    public Post(){

    }
    public Post(String nickname, String titulo, String categoria, String texto, ArrayList<Comentario> comentarios, String id) {
        this.nickname = nickname;
        this.titulo = titulo;
        this.categoria = categoria;
        this.texto = texto;
        this.comentarios = comentarios;
        this.id = id;
    }

    public Post(String nickname, String titulo, String categoria, String texto, ArrayList<Comentario> comentarios) {
        this.nickname = nickname;
        this.titulo = titulo;
        this.categoria = categoria;
        this.texto = texto;
        this.comentarios = comentarios;
        this.id = null;
    }
    public Post(String nickname, String titulo, String categoria, String texto) {
        this.nickname = nickname;
        this.titulo = titulo;
        this.categoria = categoria;
        this.texto = texto;
        this.comentarios = new ArrayList<>();
        this.id = null;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTexto() {
        return texto;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }

    public ArrayList<Comentario> getComentarios() {
        return comentarios;
    }
    public void setComentarios(ArrayList<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
