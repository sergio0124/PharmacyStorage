package com.example.pharmacystorage.helper_models;

import com.example.pharmacystorage.models.RequestAmount;
import com.example.pharmacystorage.models.RequestModel;

import java.util.List;

public class DataItems {
    private List<RequestAmount> Request_Medicines;

    public List<RequestAmount> getRequest_Medicines() {
        return Request_Medicines;
    }

    public void setRequest_Medicines(List<RequestAmount> request_Medicines) {
        this.Request_Medicines = request_Medicines;
    }
}
