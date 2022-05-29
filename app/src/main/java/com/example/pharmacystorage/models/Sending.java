package com.example.pharmacystorage.models;

import java.util.Calendar;
import java.util.Dictionary;

public class Sending {
    private int Id;
    private Calendar Date;
    private int StorageId;
    private int PharmacyId;

    public int getId() {
        return Id;
    }

    public Calendar getDate() {
        return Date;
    }

    public int getStorageId() {
        return StorageId;
    }

    public int getPharmacyId() {
        return PharmacyId;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setDate(Calendar date) {
        Date = date;
    }

    public void setStorageId(int storageId) {
        StorageId = storageId;
    }

    public void setPharmacyId(int pharmacyId) {
        PharmacyId = pharmacyId;
    }
}
