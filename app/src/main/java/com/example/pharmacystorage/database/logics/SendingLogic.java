package com.example.pharmacystorage.database.logics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pharmacystorage.database.DatabaseHelper;
import com.example.pharmacystorage.models.SendingAmount;
import com.example.pharmacystorage.models.SendingModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class SendingLogic {
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    final String TABLE = "Sending";
    final String COLUMN_ID = "Id";
    final String COLUMN_DATE = "Date";
    final String COLUMN_STORAGE_ID = "StorageId";
    final String COLUMN_PHARMACY_ID = "PharmacyId";
    final String COLUMN_IS_SENT = "IsSent";

    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

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

    public List<SendingModel> getFullList() {
        Cursor cursor = db.rawQuery("select * from " + TABLE, null);
        List<SendingModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            SendingModel obj = new SendingModel();
            Calendar cal = new GregorianCalendar();

            try {
                cal.setTime(sdf.parse(cursor.getString((int) cursor.getColumnIndex(COLUMN_DATE))));// all done
            }catch (Exception ex){}

            obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            obj.setDate(cal);
            obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));
            obj.setPharmacyId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_PHARMACY_ID)));
            obj.setSent(cursor.getInt((byte) cursor.getColumnIndex(COLUMN_IS_SENT)));

            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public List<SendingModel> getFilteredList(int pharmacyId) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_PHARMACY_ID + " = " + pharmacyId, null);
        List<SendingModel> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            SendingModel obj = getElement(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
            list.add(obj);
            cursor.moveToNext();
        } while (!cursor.isAfterLast());
        return list;
    }

    public SendingModel getElement(int id) {
        Cursor cursor = db.rawQuery("select * from " + TABLE + " where "
                + COLUMN_ID + " = " + id, null);
        SendingModel obj = new SendingModel();
        if (!cursor.moveToFirst()) {
            return null;
        }
        Calendar cal = new GregorianCalendar();

        try {
            cal.setTime(sdf.parse(cursor.getString((int) cursor.getColumnIndex(COLUMN_DATE))));// all done
        }catch (Exception ex){}

        obj.setId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_ID)));
        obj.setDate(cal);
        obj.setStorageId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_STORAGE_ID)));
        obj.setPharmacyId(cursor.getInt((int) cursor.getColumnIndex(COLUMN_PHARMACY_ID)));
        obj.setSent(cursor.getInt((byte) cursor.getColumnIndex(COLUMN_IS_SENT)));

        return obj;
    }

    public void insert(SendingModel model) {
        ContentValues content = new ContentValues();
        content.put(COLUMN_DATE,sdf.format(model.getDate().getTime()));
        content.put(COLUMN_STORAGE_ID,model.getStorageId());
        content.put(COLUMN_PHARMACY_ID,model.getPharmacyId());
        content.put(COLUMN_IS_SENT,model.isSent());
        if(model.getId() != 0){
            content.put(COLUMN_ID, model.getId());
        }
        db.insert(TABLE,null,content);
    }

    public void update(SendingModel model) {
        ContentValues content=new ContentValues();
        content.put(COLUMN_DATE, sdf.format(model.getDate().getTime()));
        content.put(COLUMN_STORAGE_ID,model.getStorageId());
        content.put(COLUMN_PHARMACY_ID,model.getPharmacyId());
        content.put(COLUMN_IS_SENT,model.isSent());
        String where = COLUMN_ID + " = " + model.getId();
        db.update(TABLE,content,where,null);
    }

    public void delete(int id) {
        String where = COLUMN_ID+" = "+id;
        db.delete(TABLE,where,null);
    }

    public void insertSendingAmounts(List<SendingAmount> models) {
        models.stream().forEach(v->{
            ContentValues content = new ContentValues();
            content.put("SendingId", v.getSendingId());
            content.put("MedicineId", v.getMedicineId());
            content.put("Cost", v.getCost());
            content.put("Quantity", v.getQuantity());
            db.insert("Sending_Medicine", null, content);
        });

    }

    public List<SendingAmount> getSendingAmountsById(int sendingId) {

        Cursor cursor = db.rawQuery("SELECT Id, SendingId, Cost, MedicineId, Quantity, Name, Dosage, " +
                "Form FROM Sending_Medicine JOIN Medicine ON MedicineId = Medicine.Id AND SendingId = " + sendingId, null);
        ArrayList<SendingAmount> list = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return list;
        }
        do {
            SendingAmount obj = new SendingAmount();

            obj.setId(cursor.getInt((int) cursor.getColumnIndex("Id")));
            obj.setSendingId(cursor.getInt((int) cursor.getColumnIndex("SendingId")));
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
