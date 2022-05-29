package com.example.pharmacystorage.models;

public class MedicineAmount {

    int Id;
    int Quantity;
    int Cost;

    public void setId(int id) {
        Id = id;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public void setCost(int cost) {
        Cost = cost;
    }

    public int getId() {
        return Id;
    }

    public int getQuantity() {
        return Quantity;
    }

    public int getCost() {
        return Cost;
    }
}
