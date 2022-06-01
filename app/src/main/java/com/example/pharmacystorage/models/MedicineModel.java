package com.example.pharmacystorage.models;

import java.io.Serializable;

public class MedicineModel implements Serializable {

    int Id = 0;
    String Name;
    int Dosage;
    String Form;
    int ManufacturerId = 0;

    public MedicineModel(int id, String name, int dosage, String form, int manufacturerId) {
        Id = id;
        Name = name;
        Dosage = dosage;
        Form = form;
        ManufacturerId = manufacturerId;
    }

    public MedicineModel() {
    }

    public MedicineModel(String name, int dosage, String form) {
        Name = name;
        Dosage = dosage;
        Form = form;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDosage(int dosage) {
        Dosage = dosage;
    }

    public void setForm(String form) {
        Form = form;
    }

    public void setManufacturerId(int manufacturerId) {
        ManufacturerId = manufacturerId;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public int getDosage() {
        return Dosage;
    }

    public String getForm() {
        return Form;
    }

    public int getManufacturerId() {
        return ManufacturerId;
    }

    @Override
    public String toString() {
        return Name + ", " + Dosage + "мл, " + Form;
    }
}
