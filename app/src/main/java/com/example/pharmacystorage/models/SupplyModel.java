package com.example.pharmacystorage.models;

import java.util.Calendar;

public class SupplyModel {

    int Id;
    Calendar Date;
    int StorageId;

    public void setId(int id) {
        Id = id;
    }

    public void setDate(Calendar date) {
        Date = date;
    }

    public void setStorageId(int storageId) {
        StorageId = storageId;
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
}
