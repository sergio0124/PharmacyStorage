package com.example.pharmacystorage.models;

public class Manufacturer {

    int Id;
    int StorageId;
    String Name;
    String Email;
    String Address;

    public void setId(int id) {
        Id = id;
    }

    public void setStorageId(int storageId) {
        StorageId = storageId;
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

    public int getId() {
        return Id;
    }

    public int getStorageId() {
        return StorageId;
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
}
