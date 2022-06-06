package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;

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
}
