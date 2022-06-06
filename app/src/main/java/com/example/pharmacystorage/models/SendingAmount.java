package com.example.pharmacystorage.models;

import java.io.Serializable;

public class SendingAmount implements Serializable {

    int Id;
    int MedicineId;
    int SendingId;
    int Quantity;
    int Cost;
    String Name;
    String Status;

    public SendingAmount(int medicineId, int sendingId, int quantity, int cost, String name, String status) {
        MedicineId = medicineId;
        SendingId = sendingId;
        Quantity = quantity;
        Cost = cost;
        Name = name;
        Status = status;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public SendingAmount() {
    }

    public SendingAmount(RequestAmount requestAmount) {
        MedicineId = requestAmount.getMedicineId();
        Quantity = requestAmount.getQuantity();
        Cost = requestAmount.getCost();
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getMedicineId() {
        return MedicineId;
    }

    public void setMedicineId(int medicineId) {
        MedicineId = medicineId;
    }

    public int getSendingId() {
        return SendingId;
    }

    public void setSendingId(int sendingId) {
        SendingId = sendingId;
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



}
