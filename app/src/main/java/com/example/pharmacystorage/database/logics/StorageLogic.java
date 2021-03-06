package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.StorageModel;

import java.util.ArrayList;
import java.util.List;

public class StorageLogic {
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "Storage";
    final String COLUMN_ID = "Id";
    final String COLUMN_NAME = "Name";
    final String COLUMN_PASSWORD = "Password";
    final String COLUMN_EMAIL = "Email";
    final String COLUMN_EMAIL_PASSWORD = "EmailPassword";

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

    public List<StorageModel> getFullList() {
        Cursor cursor = db.rawQuery("select * from " + TABLE, null);
        List<StorageModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            StorageModel obj = new StorageModel();

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

    public List<StorageModel> getFilteredList(int manufactureId) {
            return null;
    }

    public StorageModel getElement(int id) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_ID + " = " + id, null);
        StorageModel obj = new StorageModel();
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

    public void insert(StorageModel model) {
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

    public void update(StorageModel model) {
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
