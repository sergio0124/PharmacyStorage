package com.example.pharmacystorage.models;

public class Storage {

    private int Id;
    private String Name;
    private String Password;
    private String Email;
    private String EmailPassword;

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setEmailPassword(String emailPassword) {
        EmailPassword = emailPassword;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return Password;
    }

    public String getEmail() {
        return Email;
    }

    public String getEmailPassword() {
        return EmailPassword;
    }
}
