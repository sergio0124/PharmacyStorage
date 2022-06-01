package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.ManufacturerModel;

import java.util.ArrayList;
import java.util.List;

public class ManufacturerLogic {
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "Manufacturer";
    final String COLUMN_ID = "Id";
    final String COLUMN_NAME = "Name";
    final String COLUMN_EMAIL = "Email";
    final String COLUMN_ADDRESS = "Address";
    final String COLUMN_STORAGE_ID = "StorageId";

    public ManufacturerLogic(Context context) {
        sqlHelper = new DatabaseHelper(context);
        db = sqlHelper.getWritableDatabase();
    }

    //db.rawQuery("insert into Manufacturer(name, address, storageId) values ('aaa','111', 0)", null)

    public ManufacturerLogic open() {
        db = sqlHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public List<ManufacturerModel> getFullList() {
        Cursor cursor = db.rawQuery("select * from " + TABLE, null);
        List<ManufacturerModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            ManufacturerModel obj = new ManufacturerModel();

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

    public List<ManufacturerModel> getFilteredList(int storageId) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_STORAGE_ID + " = " + storageId, null);
        List<ManufacturerModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            ManufacturerModel obj = getElement(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public ManufacturerModel getElement(int id) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_ID + " = " + id, null);
        ManufacturerModel obj = new ManufacturerModel();
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

    public void insert(ManufacturerModel model) {
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

    public void update(ManufacturerModel model) {
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
