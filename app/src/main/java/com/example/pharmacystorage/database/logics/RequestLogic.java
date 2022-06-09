package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.RequestAmount;
import com.example.pharmacystorage.models.RequestModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class RequestLogic {

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "Request";
    final String COLUMN_ID = "Id";
    final String COLUMN_DATE = "Date";
    final String COLUMN_STORAGE_ID = "StorageId";
    final String COLUMN_MANUFACTURER_NAME = "Name";
    final String COLUMN_MANUFACTURER_ID = "ManufacturerId";
    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    public RequestLogic(Context context) {
        this.sqlHelper = new DatabaseHelper(context);
        this.db = sqlHelper.getReadableDatabase();
        sdf.setTimeZone(GregorianCalendar.getInstance().getTimeZone());
    }

    public RequestLogic open() {
        db = sqlHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public List<RequestModel> getFullList() {
        Cursor cursor = db.rawQuery("select * from " + TABLE, null);
        List<RequestModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            RequestModel obj = new RequestModel();
            Calendar cal = new GregorianCalendar();

            try {
                cal.setTime(sdf.parse(cursor.getString((int) cursor.getColumnIndex(COLUMN_DATE))));
            } catch (Exception ex) {
            }

            obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            obj.setDate(cal);
            obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));
            obj.setManufacturerId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_MANUFACTURER_ID)));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public List<RequestModel> getFilteredList(int storageId) {

        Cursor cursor = db.rawQuery("select Request.Id, Date, Request.StorageId, ManufacturerId, " +
                "Manufacturer.Name from Request JOIN Manufacturer ON " +
                "ManufacturerId = Manufacturer.Id AND Request.StorageId = " + storageId, null);
        List<RequestModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            RequestModel obj = new RequestModel();
            Calendar cal = new GregorianCalendar();

            try {
                cal.setTime(sdf.parse(cursor.getString((int) cursor.getColumnIndex(COLUMN_DATE))));
            } catch (Exception ex) {
            }

            obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            obj.setDate(cal);
            obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));
            obj.setManufacturerId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_MANUFACTURER_ID)));
            obj.setManufacturerName(cursor.getString((int) cursor.getColumnIndex("Name")));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public RequestModel getElement(int id) {

        Cursor cursor = db.rawQuery("select Request.Id, Date, Request.StorageId, ManufacturerId, " +
                "Manufacturer.Name from Request JOIN Manufacturer ON " +
                "ManufacturerId = Manufacturer.Id AND Request.Id = " + id, null);
        if (!cursor.moveToFirst()) {
            return null;
        }
        RequestModel obj = new RequestModel();
        Calendar cal = new GregorianCalendar();

        try {
            cal.setTime(sdf.parse(cursor.getString((int) cursor.getColumnIndex(COLUMN_DATE))));
        } catch (Exception ex) {
        }

        obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
        obj.setDate(cal);
        obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));
        obj.setManufacturerId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_MANUFACTURER_ID)));
        obj.setManufacturerName(cursor.getString(cursor.getColumnIndex(COLUMN_MANUFACTURER_NAME)));

        return obj;
    }

    public void insert(RequestModel model) {
        ContentValues content = new ContentValues();
        sdf.setTimeZone(model.getDate().getTimeZone());
        content.put(COLUMN_DATE, sdf.format(model.getDate().getTime()));
        content.put(COLUMN_STORAGE_ID, model.getStorageId());
        content.put(COLUMN_MANUFACTURER_ID, model.getManufacturerId());
        db.insert(TABLE, null, content);
    }

    public void update(RequestModel model) {
        ContentValues content = new ContentValues();
        sdf.setTimeZone(model.getDate().getTimeZone());
        content.put(COLUMN_DATE, sdf.format(model.getDate().getTime()));
        content.put(COLUMN_STORAGE_ID, model.getStorageId());
        content.put(COLUMN_ID, model.getId());
        content.put(COLUMN_MANUFACTURER_ID, model.getManufacturerId());
        String where = COLUMN_ID + " = " + model.getId();
        db.update(TABLE, content, where, null);
    }

    public void delete(int id) {
        String where = COLUMN_ID + " = " + id;
        db.delete(TABLE, where, null);
    }

    public void insertRequestMedicines(ArrayList<RequestAmount> requestAmounts) {
        requestAmounts.stream().forEach(v -> {
            ContentValues content = new ContentValues();
            content.put("RequestId", v.getRequestId());
            content.put("MedicineId", v.getMedicineId());
            content.put("Cost", v.getCost());
            content.put("Quantity", v.getQuantity());
            db.insert("Request_Medicine", null, content);
        });
    }

    public ArrayList<RequestAmount> getRequestAmountsById(int requestId) {
        Cursor cursor = db.rawQuery("SELECT RequestId, Cost, MedicineId, Quantity, Name, Dosage, " +
                "Form FROM Request_Medicine JOIN Medicine ON MedicineId = Medicine.Id AND RequestId = " + requestId, null);
        ArrayList<RequestAmount> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            RequestAmount obj = new RequestAmount();

            obj.setRequestId(cursor.getInt((int) cursor.getColumnIndex("RequestId")));
            obj.setCost(cursor.getInt((int) cursor.getColumnIndex("Cost")));
            obj.setQuantity(cursor.getInt((int) cursor.getColumnIndex("Quantity")));
            obj.setMedicineId(cursor.getInt((int) cursor.getColumnIndex("MedicineId")));
            String name = cursor.getString((int) cursor.getColumnIndex("Name")) + ", " +
                    cursor.getString((int) cursor.getColumnIndex("Dosage")) + ", " +
                    cursor.getString((int) cursor.getColumnIndex("Form"));
            obj.setName(name);

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }
}
