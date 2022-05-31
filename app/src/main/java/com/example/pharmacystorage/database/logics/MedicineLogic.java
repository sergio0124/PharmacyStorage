package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.MedicineModel;
import com.example.pharmacystorage.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MedicineLogic {
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "medicine";
    final String COLUMN_ID = "id";
    final String COLUMN_NAME = "name";
    final String COLUMN_DOSAGE = "dosage";
    final String COLUMN_FORM = "form";
    final String COLUMN_MANUFACTURE_ID = "manufactureId";

    public MedicineLogic(Context context) {
        sqlHelper = new DatabaseHelper(context);
        db = sqlHelper.getWritableDatabase();
    }

    public MedicineLogic open() {
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
            obj.setDosage(cursor.getInt((int) cursor.getColumnIndex(COLUMN_DOSAGE)));
            obj.setForm(cursor.getString((int) cursor.getColumnIndex(COLUMN_FORM)));
            obj.setManufactureId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_MANUFACTURE_ID)));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public List<MedicineModel> getFilteredList(int manufactureId) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_MANUFACTURE_ID + " = " + manufactureId, null);
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
        obj.setName(cursor.getString((int) cursor.getColumnIndex(COLUMN_NAME)));
        obj.setDosage(cursor.getInt((int) cursor.getColumnIndex(COLUMN_DOSAGE)));
        obj.setForm(cursor.getString((int) cursor.getColumnIndex(COLUMN_FORM)));
        obj.setManufactureId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_MANUFACTURE_ID)));

        return obj;
    }

    public void insert(MedicineModel model) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_NAME,model.getName());
        content.put(COLUMN_DOSAGE,model.getDosage());
        content.put(COLUMN_FORM,model.getForm());
        content.put(COLUMN_MANUFACTURE_ID,model.getManufactureId());
        if(model.getId() != 0){
            content.put(COLUMN_ID, model.getId());
        }
        db.insert(TABLE,null,content);
    }

    public void update(MedicineModel model) {
        ContentValues content=new ContentValues();
        content.put(COLUMN_NAME,model.getName());
        content.put(COLUMN_DOSAGE,model.getDosage());
        content.put(COLUMN_FORM,model.getForm());
        content.put(COLUMN_MANUFACTURE_ID,model.getManufactureId());
        String where = COLUMN_ID + " = " + model.getId();
        db.update(TABLE,content,where,null);
    }

    public void delete(int id) {
        String where = COLUMN_ID+" = "+id;
        db.delete(TABLE,where,null);
    }
}
