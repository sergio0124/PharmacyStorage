package com.example.pharmacystorage.helper_models;

import com.example.pharmacystorage.models.RequestAmount;

import java.util.List;

public class DataItems<T> {
    private List<T> collection;

    public List<T> getCollection() {
        return collection;
    }

    public void setCollection(List<T> collection) {
        this.collection = collection;
    }
}
