package com.example.pharmacystorage.models;

public class SendingAmount {

    int Id;
    int MedicineId;
    int SendingId;
    int Quantity;
    int Cost;

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
