package com.example.forodepancho.entidad;

public class Usuario {

    private String nickname;
    private String correo;
    private boolean mod;

    public Usuario() {

    }

    public Usuario(String nickname, String correo, boolean mod) {
        this.nickname = nickname;
        this.correo = correo;
        this.mod = mod;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isMod() {
        return mod;
    }

    public void setMod(boolean mod) {
        this.mod = mod;
    }
}
