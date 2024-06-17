package com.example.forodepancho.entidad;

import java.io.Serializable;

public class Comentario implements Serializable {

    private String nickname;
    private String texto;

    public Comentario() {

    }

    public Comentario(String nickname, String texto) {
        this.nickname = nickname;
        this.texto = texto;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTexto() {
        return texto;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }
}
