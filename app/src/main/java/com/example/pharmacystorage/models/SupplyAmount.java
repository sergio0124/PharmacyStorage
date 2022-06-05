package com.example.pharmacystorage.models;

import java.io.Serializable;
import java.util.Calendar;

public class SupplyAmount implements Serializable {

    int SupplyId;
    int MedicineId;
    String Name;
    int Quantity;
    int Cost;
    String State;
    Calendar EndDate;

    public Calendar getEndDate() {
        return EndDate;
    }

    public void setEndDate(Calendar endDate) {
        EndDate = endDate;
    }

    public SupplyAmount(int medicineId, String name, int quantity, int cost, String state) {
        MedicineId = medicineId;
        Name = name;
        Quantity = quantity;
        Cost = cost;
        State = state;
    }

    public int getSupplyId() {
        return SupplyId;
    }

    public void setSupplyId(int supplyId) {
        SupplyId = supplyId;
    }

    public int getMedicineId() {
        return MedicineId;
    }

    public void setMedicineId(int medicineId) {
        MedicineId = medicineId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getCost() {
        return Cost;
    }

    public void setCost(int cost) {
        Cost = cost;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }




}
