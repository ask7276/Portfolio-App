package com.example.portfolioapp.Models;

public class User {
    private String Email;

    public User() {
    }

    public User(String email) {
        Email = email;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }
}
