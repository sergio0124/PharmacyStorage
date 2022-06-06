package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.MedicineModel;
import com.example.pharmacystorage.models.RequestAmount;

import java.util.ArrayList;
import java.util.List;

public class BasketLogic {

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "Basket";
    final String COLUMN_ID = "Id";
    final String COLUMN_STORAGE_ID = "StorageId";

    public BasketLogic(Context context) {
        sqlHelper = new DatabaseHelper(context);
        db = sqlHelper.getWritableDatabase();
    }

    public BasketLogic open() {
        db = sqlHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public void createBasket(int userId){
        ContentValues content = new ContentValues();
        content.put(COLUMN_STORAGE_ID, userId);
        db.insert(TABLE, null, content);
    }

    public void insertMedicineById(int medicineId, int userId){
        Cursor cursor = db.rawQuery("select * from " + TABLE + " JOIN Medicine_Basket ON Medicine_Basket.StorageId = Storage.Id AND Storage.Id = " + userId, null);
        int basketId = cursor.getInt((int) cursor.getColumnIndex("Basket.Id"));

        ContentValues content = new ContentValues();
        content.put("BasketId", basketId);
        content.put("MedicineId", medicineId);
        db.insert("Medicine_Basket", null, content);
    }

    public List<MedicineModel> getMedicinesInBasket(){
        Cursor cursor = db.rawQuery("SELECT * FROM Medicine_Basket JOIN Medicine ON Medicine.Id = Medicine_Basket.MedicineId",null);

        ArrayList<MedicineModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            MedicineModel obj = new MedicineModel();

            obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            obj.setName(cursor.getString((int) cursor.getColumnIndex("Name")));
            obj.setDosage(cursor.getInt((int) cursor.getColumnIndex("Dosage")));
            obj.setForm(cursor.getString((int) cursor.getColumnIndex("Form")));
            obj.setManufacturerId(cursor.getInt((int) cursor.getColumnIndex("ManufacturerId")));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }
}
