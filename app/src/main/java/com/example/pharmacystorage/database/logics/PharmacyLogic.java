package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.PharmacyModel;

import java.util.ArrayList;
import java.util.List;

public class PharmacyLogic {
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "Pharmacy";
    final String COLUMN_ID = "Id";
    final String COLUMN_NAME = "Name";
    final String COLUMN_EMAIL = "Email";
    final String COLUMN_ADDRESS = "Address";
    final String COLUMN_STORAGE_ID = "StorageId";

    public PharmacyLogic(Context context) {
        sqlHelper = new DatabaseHelper(context);
        db = sqlHelper.getWritableDatabase();
    }

    public PharmacyLogic open() {
        db = sqlHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public List<PharmacyModel> getFullList() {
        Cursor cursor = db.rawQuery("select * from " + TABLE, null);
        List<PharmacyModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            PharmacyModel obj = new PharmacyModel();

            obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            obj.setName(cursor.getString((int) cursor.getColumnIndex(COLUMN_NAME)));
            obj.setEmail(cursor.getString((int) cursor.getColumnIndex(COLUMN_EMAIL)));
            obj.setAddress(cursor.getString((int) cursor.getColumnIndex(COLUMN_ADDRESS)));
            obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public List<PharmacyModel> getFilteredList(int storageId) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_STORAGE_ID + " = " + storageId, null);
        List<PharmacyModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            PharmacyModel obj = getElement(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public PharmacyModel getElement(int id) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_ID + " = " + id, null);
        PharmacyModel obj = new PharmacyModel();
        if (!cursor.moveToFirst()) {
            return null;
        }

        obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
        obj.setName(cursor.getString((int) cursor.getColumnIndex(COLUMN_NAME)));
        obj.setEmail(cursor.getString((int) cursor.getColumnIndex(COLUMN_EMAIL)));
        obj.setAddress(cursor.getString((int) cursor.getColumnIndex(COLUMN_ADDRESS)));
        obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));

        return obj;
    }

    public PharmacyModel getElement(String email) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_EMAIL + " = " + "'" + email + "'", null);
        PharmacyModel obj = new PharmacyModel();
        if (!cursor.moveToFirst()) {
            return null;
        }

        obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
        obj.setName(cursor.getString((int) cursor.getColumnIndex(COLUMN_NAME)));
        obj.setEmail(cursor.getString((int) cursor.getColumnIndex(COLUMN_EMAIL)));
        obj.setAddress(cursor.getString((int) cursor.getColumnIndex(COLUMN_ADDRESS)));
        obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));

        return obj;
    }

    public void insert(PharmacyModel model) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_NAME,model.getName());
        content.put(COLUMN_EMAIL,model.getEmail());
        content.put(COLUMN_ADDRESS,model.getAddress());
        content.put(COLUMN_STORAGE_ID,model.getStorageId());
        if(model.getId() != 0){
            content.put(COLUMN_ID, model.getId());
        }
        db.insert(TABLE,null,content);
    }

    public void update(PharmacyModel model) {
        ContentValues content=new ContentValues();
        content.put(COLUMN_NAME,model.getName());
        content.put(COLUMN_EMAIL,model.getEmail());
        content.put(COLUMN_ADDRESS,model.getAddress());
        content.put(COLUMN_STORAGE_ID,model.getStorageId());
        String where = COLUMN_ID + " = " + model.getId();
        db.update(TABLE,content,where,null);
    }

    public void delete(int id) {
        String where = COLUMN_ID+" = "+id;
        db.delete(TABLE,where,null);
    }
}
