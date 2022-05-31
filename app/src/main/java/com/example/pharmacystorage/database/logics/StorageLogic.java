package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class StorageLogic {
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "storage";
    final String COLUMN_ID = "id";
    final String COLUMN_NAME = "name";
    final String COLUMN_PASSWORD = "password";
    final String COLUMN_EMAIL = "email";
    final String COLUMN_EMAIL_PASSWORD = "emailPassword";

    public StorageLogic(Context context) {
        sqlHelper = new DatabaseHelper(context);
        db = sqlHelper.getWritableDatabase();
    }

    public StorageLogic open() {
        db = sqlHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public List<MedicineModel> getFullList() {
        Cursor cursor = db.rawQuery("select * from " + TABLE, null);
        List<MedicineModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            MedicineModel obj = new MedicineModel();

            obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            obj.setName(cursor.getString((int) cursor.getColumnIndex(COLUMN_NAME)));
            obj.setPassword(cursor.getString((int) cursor.getColumnIndex(COLUMN_PASSWORD)));
            obj.setEmail(cursor.getString((int) cursor.getColumnIndex(COLUMN_EMAIL)));
            obj.setEmailPassword(cursor.getString((int) cursor.getColumnIndex(COLUMN_EMAIL_PASSWORD)));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public List<MedicineModel> getFilteredList(int manufactureId) {
            return null;
    }

    public MedicineModel getElement(int id) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_ID + " = " + id, null);
        MedicineModel obj = new MedicineModel();
        if (!cursor.moveToFirst()) {
            return null;
        }

        obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
        obj.setName(cursor.getString((int) cursor.getColumnIndex(COLUMN_NAME)));
        obj.setPassword(cursor.getString((int) cursor.getColumnIndex(COLUMN_PASSWORD)));
        obj.setEmail(cursor.getString((int) cursor.getColumnIndex(COLUMN_EMAIL)));
        obj.setEmailPassword(cursor.getString((int) cursor.getColumnIndex(COLUMN_EMAIL_PASSWORD)));

        return obj;
    }

    public void insert(MedicineModel model) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_NAME,model.getName());
        content.put(COLUMN_PASSWORD,model.getPassword());
        content.put(COLUMN_EMAIL,model.getEmail());
        content.put(COLUMN_EMAIL_PASSWORD,model.getEmailPassword());
        if(model.getId() != 0){
            content.put(COLUMN_ID, model.getId());
        }
        db.insert(TABLE,null,content);
    }

    public void update(MedicineModel model) {
        ContentValues content=new ContentValues();
        content.put(COLUMN_NAME,model.getName());
        content.put(COLUMN_PASSWORD,model.getPassword());
        content.put(COLUMN_EMAIL,model.getEmail());
        content.put(COLUMN_EMAIL_PASSWORD,model.getEmailPassword());
        String where = COLUMN_ID + " = " + model.getId();
        db.update(TABLE,content,where,null);
    }

    public void delete(int id) {
        String where = COLUMN_ID+" = "+id;
        db.delete(TABLE,where,null);
    }
}
