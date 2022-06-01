package com.example.pharmacystorage.models;

import androidx.annotation.NonNull;

public class ManufacturerModel {

    int Id;
    int StorageId;
    String Name;
    String Email;
    String Address;

    public ManufacturerModel() {
    }

    public ManufacturerModel(String name, String email, String address, int storageId) {
        StorageId = storageId;
        Name = name;
        Email = email;
        Address = address;
    }

    public ManufacturerModel(int id, int storageId, String name, String email, String address) {
        Id = id;
        StorageId = storageId;
        Name = name;
        Email = email;
        Address = address;
    }

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

    @Override
    public String toString(){
        return Name;
    }
}
