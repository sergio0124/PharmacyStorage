package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.MedicineModel;

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
        Cursor cursor = db.rawQuery("select * from " + TABLE + " JOIN Storage ON StorageId = Storage.Id AND Storage.Id = " + userId, null);
        cursor.moveToFirst();
        int basketId = cursor.getInt((int) cursor.getColumnIndex("Basket.Id"));

        if(checkIfExist(basketId, medicineId)){
            return;
        }

        ContentValues content = new ContentValues();
        content.put("BasketId", basketId);
        content.put("MedicineId", medicineId);
        db.insert("Medicine_Basket", null, content);
    }

    public List<MedicineModel> getMedicinesInBasket(int userId){
        int basketId = getBasketId(userId);
        Cursor cursor = db.rawQuery("SELECT * FROM Medicine_Basket JOIN Medicine ON Medicine.Id = Medicine_Basket.MedicineId AND Medicine_Basket.BasketId = " + basketId,null);

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

    public int getBasketId(int userId){
        Cursor cursor = db.rawQuery("select * from " + TABLE + " JOIN Storage ON StorageId = Storage.Id AND Storage.Id = " + userId, null);
        cursor.moveToFirst();
        return cursor.getInt((int) cursor.getColumnIndex("Basket.Id"));
    }

    private boolean checkIfExist(int basketId, int medicineId){
        Cursor cursor = db.rawQuery("select * from " + TABLE + " JOIN Medicine_Basket ON Medicine_Basket.BasketId = Basket.Id" +
                " AND MedicineId = " + medicineId + " AND BasketId = " + basketId, null);
        return cursor.moveToFirst();
    }

    public void deleteMedicineFromDatabase(int medicineId, int userId){
        int basketId = getBasketId(userId);
        String where =" MedicineId = " + medicineId + " AND BasketId = " + basketId;
        db.delete("Medicine_Basket", where, null);
    }
}
