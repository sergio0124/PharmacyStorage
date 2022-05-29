package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SendingLogic {
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "medicine";
    final String COLUMN_ID = "id";
    final String COLUMN_DATE = "date";
    final String COLUMN_STORAGE_ID = "storageId";
    final String COLUMN_PHARMACY_ID = "pharmacyId";

    public SendingLogic(Context context) {
        sqlHelper = new DatabaseHelper(context);
        db = sqlHelper.getWritableDatabase();
    }

    public SendingLogic open() {
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
            obj.setDate(cursor.getString((int) cursor.getColumnIndex(COLUMN_DATE)));
            obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));
            obj.setPharmacyId(cursor.getString((int) cursor.getColumnIndex(COLUMN_PHARMACY_ID)));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public List<MedicineModel> getFilteredList(int pharmacyId) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_PHARMACY_ID + " = " + pharmacyId, null);
        List<MedicineModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            MedicineModel obj = getElement(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public MedicineModel getElement(int id) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_ID + " = " + id, null);
        MedicineModel obj = new MedicineModel();
        if (!cursor.moveToFirst()) {
            return null;
        }

        obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
        obj.setDate(cursor.getString((int) cursor.getColumnIndex(COLUMN_DATE)));
        obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));
        obj.setPharmacyId(cursor.getString((int) cursor.getColumnIndex(COLUMN_PHARMACY_ID)));

        return obj;
    }

    public void insert(MedicineModel model) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_DATE,model.getName());
        content.put(COLUMN_STORAGE_ID,model.getStorageId());
        content.put(COLUMN_PHARMACY_ID,model.getPharmacyId());
        if(model.getId() != 0){
            content.put(COLUMN_ID, model.getId());
        }
        db.insert(TABLE,null,content);
    }

    public void update(MedicineModel model) {
        ContentValues content=new ContentValues();
        content.put(COLUMN_DATE,model.getName());
        content.put(COLUMN_STORAGE_ID,model.getStorageId());
        content.put(COLUMN_PHARMACY_ID,model.getPharmacyId());
        String where = COLUMN_ID + " = " + model.getId();
        db.update(TABLE,content,where,null);
    }

    public void delete(int id) {
        String where = COLUMN_ID+" = "+id;
        db.delete(TABLE,where,null);
    }
}
