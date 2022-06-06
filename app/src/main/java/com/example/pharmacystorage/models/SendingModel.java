package com.example.pharmacystorage.models;

import java.util.Calendar;

public class SendingModel {
    private int Id;
    private Calendar Date;
    private int StorageId;
    private int PharmacyId;
    private String PharmacyName;
    int isSent = 0;


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

    public String getPharmacyName() {
        return PharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        PharmacyName = pharmacyName;
    }

    public int isSent() {
        if (isSent > 0) return 1;
        return 0;
    }

    public void setSent(int sent) {
        isSent = sent;
    }
}
