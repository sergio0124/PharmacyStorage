package com.example.pharmacystorage.models;

public class MedicineModel {

    int Id;
    String Name;
    int Dosage;
    String Form;
    int ManufacturerId;

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
}
