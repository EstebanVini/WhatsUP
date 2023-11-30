package com.example.cliente;

public class User {
    private String username;
    private String password;
    private String phone;

    private String RutaCertificado;

    public User(String username, String password, String phoneText) {
        this.username = username;
        this.password = password;
        this.phone = phoneText;
        this.RutaCertificado = "";
    }


    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {return password; }

    public String getRutaCertificado() {return RutaCertificado; }

}