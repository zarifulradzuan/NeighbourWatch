package com.example.neighbourwatch.model;

public class User {
    private String email;
    private String password;

    public User(){}

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
