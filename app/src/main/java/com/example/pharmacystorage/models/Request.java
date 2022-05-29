package com.example.pharmacystorage.models;

import java.util.Calendar;

public class Request {

    int Id;
    Calendar Date;
    int StorageId;
    int ManufacturerId;

    public void setId(int id) {
        Id = id;
    }

    public void setDate(Calendar date) {
        Date = date;
    }

    public void setStorageId(int storageId) {
        StorageId = storageId;
    }

    public void setManufacturerId(int manufacturerId) {
        ManufacturerId = manufacturerId;
    }

    public int getId() {
        return Id;
    }

    public Calendar getDate() {
        return Date;
    }

    public int getStorageId() {
        return StorageId;
    }

    public int getManufacturerId() {
        return ManufacturerId;
    }
}
