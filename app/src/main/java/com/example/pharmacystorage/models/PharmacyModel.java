package com.example.pharmacystorage.models;

public class PharmacyModel {

    private int Id;
    private String Name;
    private String Email;
    private String Address;
    private int StorageId;

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setStorageId(int storageId) {
        StorageId = storageId;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getAddress() {
        return Address;
    }

    public int getStorageId() {
        return StorageId;
    }
}
