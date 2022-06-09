package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.SupplyAmount;
import com.example.pharmacystorage.models.SupplyModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class SupplyLogic {

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "Supply";
    final String COLUMN_ID = "Id";
    final String COLUMN_DATE = "Date";
    final String COLUMN_STORAGE_ID = "StorageId";
    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);


    public SupplyLogic(Context context) {
        sqlHelper = new DatabaseHelper(context);
        db = sqlHelper.getWritableDatabase();
    }

    public SupplyLogic open() {
        db = sqlHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public void insert(SupplyModel model) {
        ContentValues content = new ContentValues();
        sdf.setTimeZone(model.getDate().getTimeZone());
        content.put(COLUMN_DATE, sdf.format(model.getDate().getTime()));
        content.put(COLUMN_STORAGE_ID, model.getStorageId());
        if (model.getId() != 0) {
            content.put(COLUMN_ID, model.getId());
        }
        db.insert(TABLE, null, content);
    }


    public List<SupplyModel> getFilteredByStorageList(int userId) {

        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_STORAGE_ID + " = " + userId, null);
        List<SupplyModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            SupplyModel obj = new SupplyModel();
            Calendar cal = new GregorianCalendar();
            try {
                cal.setTime(sdf.parse(cursor.getString((int) cursor.getColumnIndex(COLUMN_DATE))));
            } catch (Exception ex) {
            }

            obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            obj.setDate(cal);
            obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }


    public void insertSupplyAmounts(List<SupplyAmount> supplyAmounts) {
        supplyAmounts.stream().forEach(v -> {
            ContentValues content = new ContentValues();
            content.put("SupplyId", v.getSupplyId());
            content.put("MedicineId", v.getMedicineId());
            content.put("Cost", v.getCost());
            sdf.setTimeZone(v.getEndDate().getTimeZone());
            content.put("EndDate", sdf.format(v.getEndDate().getTime()));
            content.put("Quantity", v.getQuantity());
            content.put("CurrentQuantity", v.getQuantity());
            content.put("isEmpty", 0);
            db.insert("Medicine_Supply", null, content);
        });
    }


    public List<SupplyAmount> getSupplyAmountsByStorage(int userId) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " JOIN Medicine_Supply " +
                        "ON Medicine_Supply.SupplyId = Supply.Id AND StorageId = " + userId, null);
        List<SupplyAmount> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {

            SupplyAmount obj = new SupplyAmount();
            Calendar cal = new GregorianCalendar();
            try {
                cal.setTime(sdf.parse(cursor.getString((int) cursor.getColumnIndex("EndDate"))));
            } catch (Exception ex) {
            }

            obj.setSupplyId(cursor.getInt((int) cursor.getColumnIndex("SupplyId")));
            obj.setMedicineId(cursor.getInt((int) cursor.getColumnIndex("MedicineId")));
            obj.setEndDate(cal);
            obj.setCost(cursor.getInt((int) cursor.getColumnIndex("Cost")));
            obj.setQuantity(cursor.getInt((int) cursor.getColumnIndex("Quantity")));
            obj.setId(cursor.getInt((int) cursor.getColumnIndex("Id")));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public List<SupplyAmount> getSupplyAmountsBySupplyAndDate(int supplyId, long from, long to) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " JOIN Medicine_Supply " +
                "ON Medicine_Supply.SupplyId = Supply.Id AND Supply.Id = " + supplyId, null);
        List<SupplyAmount> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {

            SupplyAmount obj = new SupplyAmount();
            Calendar cal = new GregorianCalendar();
            try {
                cal.setTime(sdf.parse(cursor.getString((int) cursor.getColumnIndex("EndDate"))));
            } catch (Exception ex) {
            }

            Calendar calDate = new GregorianCalendar();
            try {
                calDate.setTime(sdf.parse(cursor.getString((int) cursor.getColumnIndex("Date"))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(from >= calDate.getTimeInMillis() || calDate.getTimeInMillis() >= to){
                continue;
            }

            obj.setSupplyId(cursor.getInt((int) cursor.getColumnIndex("SupplyId")));
            obj.setMedicineId(cursor.getInt((int) cursor.getColumnIndex("MedicineId")));
            obj.setEndDate(cal);
            obj.setCost(cursor.getInt((int) cursor.getColumnIndex("Cost")));
            obj.setQuantity(cursor.getInt((int) cursor.getColumnIndex("Quantity")));
            obj.setId(cursor.getInt((int) cursor.getColumnIndex("Id")));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public void updateSupplyAmount(SupplyAmount supplyAmount) {

        ContentValues content = new ContentValues();
        content.put("SupplyId", supplyAmount.getSupplyId());
        content.put("MedicineId", supplyAmount.getMedicineId());
        content.put("Cost", supplyAmount.getCost());
        sdf.setTimeZone(supplyAmount.getEndDate().getTimeZone());
        content.put("EndDate", sdf.format(supplyAmount.getEndDate().getTime()));
        content.put("Quantity", supplyAmount.getQuantity());
        content.put("CurrentQuantity", supplyAmount.getOldQuantity());
        content.put("isEmpty", supplyAmount.getIsEmpty());
        String where = COLUMN_ID + " = " + supplyAmount.getId();
        db.update("Medicine_Supply", content, where, null);

    }
}
