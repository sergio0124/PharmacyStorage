package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.MedicineModel;

import java.util.ArrayList;
import java.util.List;

public class MedicineLogic {
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "Medicine";
    final String COLUMN_ID = "Id";
    final String COLUMN_NAME = "Name";
    final String COLUMN_DOSAGE = "Dosage";
    final String COLUMN_FORM = "Form";
    final String COLUMN_MANUFACTURER_ID = "ManufacturerId";

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
            obj.setManufacturerId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_MANUFACTURER_ID)));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public List<MedicineModel> getFilteredList(int manufactureId) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_MANUFACTURER_ID + " = " + manufactureId, null);
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
        obj.setManufacturerId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_MANUFACTURER_ID)));

        return obj;
    }

    public void insert(MedicineModel model) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_NAME, model.getName());
        content.put(COLUMN_DOSAGE, model.getDosage());
        content.put(COLUMN_FORM, model.getForm());
        content.put(COLUMN_MANUFACTURER_ID, model.getManufacturerId());
        if (model.getId() != 0) {
            content.put(COLUMN_ID, model.getId());
        }
        db.insert(TABLE, null, content);
    }

    public void update(MedicineModel model) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_NAME, model.getName());
        content.put(COLUMN_DOSAGE, model.getDosage());
        content.put(COLUMN_FORM, model.getForm());
        content.put(COLUMN_MANUFACTURER_ID, model.getManufacturerId());
        String where = COLUMN_ID + " = " + model.getId();
        db.update(TABLE, content, where, null);
    }

    public void delete(int id) {
        String where = COLUMN_ID + " = " + id;
        db.delete(TABLE, where, null);
    }

    public MedicineModel getMedicineByFullName(String name) {
        MedicineModel model = new MedicineModel();

        String[] fields = name.split(", ");

        String MedicineName = fields[0];
        String MedicineFormat = fields[2];
        int MedicineDosage = Integer.parseInt(fields[1]);

        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_FORM + " = " + "'"+MedicineFormat+"'" + " AND Name = "
                + "'"+MedicineName+"'" + " AND Dosage = " + "'"+MedicineDosage+"'" + " LIMIT 1", null);

        if (!cursor.moveToFirst()) {
            return null;
        }
        model.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
        model.setName(cursor.getString((int) cursor.getColumnIndex(COLUMN_NAME)));
        model.setDosage(cursor.getInt((int) cursor.getColumnIndex(COLUMN_DOSAGE)));
        model.setForm(cursor.getString((int) cursor.getColumnIndex(COLUMN_FORM)));
        model.setManufacturerId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_MANUFACTURER_ID)));

        return model;

    }

    public List<MedicineModel> getFilteredListWithQuantityByStorage(int userId){

        Cursor cursor = db.rawQuery("SELECT SUM(Medicine_Supply.CurrentQuantity) QuantitySum, Medicine.Name, Medicine.Id, Dosage, Form, Manufacturer.Id ManufacturerId FROM Medicine JOIN Manufacturer ON ManufacturerId = Manufacturer.Id" +
                " JOIN Storage ON Manufacturer.StorageId = Storage.Id AND Storage.Id = " + userId +
                " JOIN Medicine_Supply ON MedicineId = Medicine.Id " +
                " GROUP BY Medicine.Id", null);

        List<MedicineModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            MedicineModel model = new MedicineModel();
            model.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            model.setName(cursor.getString((int) cursor.getColumnIndex(COLUMN_NAME)));
            model.setDosage(cursor.getInt((int) cursor.getColumnIndex(COLUMN_DOSAGE)));
            model.setForm(cursor.getString((int) cursor.getColumnIndex(COLUMN_FORM)));
            model.setManufacturerId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_MANUFACTURER_ID)));
            model.setQuantityOnStorage(cursor.getInt((int) cursor.getColumnIndex("QuantitySum")));
            list.add(model);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());


        return list;

    }
}
