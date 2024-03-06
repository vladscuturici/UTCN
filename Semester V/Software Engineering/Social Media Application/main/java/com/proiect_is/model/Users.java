package com.proiect_is.model;

public class Users {
    private int user_id;
    private String email;
    private String username;
    private String password;


    public Users(int user_id, String email, String username, String password) {
        this.user_id = user_id;
        this.email = email;
        this.username = username;
        this.password = password;

    }

    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return this.username;
    }
}
