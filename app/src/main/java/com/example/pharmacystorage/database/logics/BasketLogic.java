package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
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
}
