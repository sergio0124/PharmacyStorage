package com.example.pharmacystorage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pharmacystorage.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table Storage(\n" +
                "\tStorageId integer primary key autoincrement,\n" +
                "\tStorageName varchar(255) not null,\n" +
                "\tStoragePassword varchar(255) not null,\n" +
                "\tEmail varchar(255) not null,\n" +
                "\tEmailPassword varchar(255) not null\n" +
                ");\n");

        db.execSQL("create table Pharmacy(\n" +
                "\tPharmacyId integer primary key autoincrement,\n" +
                "\tPharmacyName varchar(255) not null,\n" +
                "\tEmail varchar(255) not null,\n" +
                "\tAddress varchar(255) not null,\n" +
                "\tStorageId int not null,\n" +
                "\tFOREIGN KEY (StorageId) \n" +
                "\tREFERENCES Storage (StorageId) ON DELETE CASCADE\n" +
                ")\n");

        db.execSQL("create table Sending(\n" +
                "\tSendingId integer primary key autoincrement,\n" +
                "\tDate varchar(255) not null,\n" +
                "\tStorageId int not null,\n" +
                "\tPharmacyId int,\n" +
                "\tFOREIGN KEY (StorageId) \n" +
                "\tREFERENCES Storage (StorageId) ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY (PharmacyId) \n" +
                "\tREFERENCES Pharmacy (PharmacyId) ON DELETE set null\n" +
                ");\n");

        db.execSQL("create table Manufacturer(\n" +
                "\tManufacturerId integer primary key autoincrement,\n" +
                "\tManufacturerName varchar(255) not null,\n" +
                "\tEmail varchar(255) not null,\n" +
                "\tAddress varchar(255) not null,\n" +
                "\tStorageId int not null,\n" +
                "\tFOREIGN KEY (StorageId) \n" +
                "\tREFERENCES Storage (StorageId) ON DELETE CASCADE\n" +
                ");\n");

        db.execSQL("create table Request(\n" +
                "\tRequestId integer primary key autoincrement,\n" +
                "\tDate varchar(255) not null,\n" +
                "\tStorageId int not null,\n" +
                "\tManufacturerId int,\n" +
                "\tFOREIGN KEY (StorageId) \n" +
                "\tREFERENCES Storage (StorageId) ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY (ManufacturerId) \n" +
                "\tREFERENCES Manufacturer (ManufacturerId) ON DELETE set null\n" +
                ");\n");

        db.execSQL("create table Basket(\n" +
                "\tBasketId integer primary key autoincrement,\n" +
                "\tStorageId int not null,\n" +
                "\tFOREIGN KEY (StorageId) \n" +
                "\tREFERENCES Storage (StorageId) ON DELETE CASCADE\n" +
                ");\n");

        db.execSQL("create table Medicine(\n" +
                "\tMedicineId integer primary key autoincrement,\n" +
                "\tMedicineName varchar(255) not null,\n" +
                "\tDosage int not null,\n" +
                "\tForm varchar(255) not null,\n" +
                "\tManufacturerId int not null,\n" +
                "\tFOREIGN KEY (ManufacturerId) \n" +
                "\tREFERENCES Manufacturer (ManufacturerId) ON DELETE CASCADE\n" +
                ");\n");

        db.execSQL("create table Supply(\n" +
                "\tSupplyId integer primary key autoincrement,\n" +
                "\tDate varchar(255) not null,\n" +
                "\tStorageId int not null,\n" +
                "\tFOREIGN KEY (StorageId) \n" +
                "\tREFERENCES Storage (StorageId) ON DELETE CASCADE\n" +
                ");\n");

        db.execSQL("create table Medicine_Basket(\n" +
                "\tMedicineId int not null,\n" +
                "\tBasketId int not null,\n" +
                "\tFOREIGN KEY (MedicineId) \n" +
                "\tREFERENCES Medicine (MedicineId) ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY (BasketId) \n" +
                "\tREFERENCES Basket (BasketId) ON DELETE CASCADE\n" +
                ");\n");

        db.execSQL("create table Request_Medicine(\n" +
                "\tMedicineId int not null,\n" +
                "\tRequestId int not null,\n" +
                "\tFOREIGN KEY (MedicineId) \n" +
                "\tREFERENCES Medicine (MedicineId) ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY (RequestId) \n" +
                "\tREFERENCES Request (RequestId) ON DELETE CASCADE\n" +
                ");\n");

        db.execSQL("create table Medicine_Supply(\n" +
                "\tId integer primary key autoincrement,\n" +
                "\tMedicineId int not null,\n" +
                "\tSupplyId int not null,\n" +
                "\tEndDate varchar(255) not null,\n" +
                "\tQuantity int not null,\n" +
                "\tCurrentQuantity int not null,\n" +
                "\tCost int not null,\n" +
                "\tIsEmpty bit not null default ('0'),\n" +
                "\t\n" +
                "\tFOREIGN KEY (MedicineId) \n" +
                "\tREFERENCES Medicine (MedicineId) ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY (SupplyId) \n" +
                "\tREFERENCES Supply (SupplyId) ON DELETE CASCADE\n" +
                ");\n");

        db.execSQL("create table Sending_Medicine(\n" +
                "\tId integer primary key autoincrement,\n" +
                "\tMedicineId int not null,\n" +
                "\tSendingId int not null,\n" +
                "\tQuantity int not null,\n" +
                "\tCost int not null,\n" +
                "\t\n" +
                "\tFOREIGN KEY (MedicineId) \n" +
                "\tREFERENCES Medicine (MedicineId) ON DELETE CASCADE,\n" +
                "\tFOREIGN KEY (SendingId) \n" +
                "\tREFERENCES Sending (SendingId) ON DELETE CASCADE\n" +
                ");\n");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public void deleteDatabase(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }
}