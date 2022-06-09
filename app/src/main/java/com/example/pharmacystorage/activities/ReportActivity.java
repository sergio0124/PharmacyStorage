package com.example.pharmacystorage.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.Report;
import com.example.pharmacystorage.database.logics.ManufacturerLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.database.logics.SupplyLogic;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ReportActivity extends AppCompatActivity {
    TextView text_view_report_info;

    Button button_date_from;
    Button button_date_to;
    Button button_report;

    Calendar dateFrom = new GregorianCalendar();
    Calendar dateTo = new GregorianCalendar();
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        button_date_from = findViewById(R.id.button_date_from);
        button_date_to = findViewById(R.id.button_date_to);
        button_report = findViewById(R.id.button_report);

        text_view_report_info = findViewById(R.id.text_view_report_info);

        Calendar calendar = Calendar.getInstance();


        dateFrom.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dateTo.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        userId = getIntent().getExtras().getInt("userId");

        String text = "Отчет по количеству продаж у каждого аптекаря в период с " + dateFrom.getTime().getDate() + " / " + dateFrom.getTime().getMonth() + " / " + (dateFrom.getTime().getYear()+ 1900) + " по " + dateTo.getTime().getDate() + " / " + dateTo.getTime().getMonth() + " / " + (dateTo.getTime().getYear()+ 1900);
        text_view_report_info.setText(text);

        button_date_from.setOnClickListener(
                v -> {
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            dateFrom.set(year, monthOfYear + 1, dayOfMonth);
                            text_view_report_info.clearComposingText();
                            String text = "Отчет по количеству продаж у каждого аптекаря в период с " + dateFrom.getTime().getDate() + " / " + dateFrom.getTime().getMonth() + " / " + (dateFrom.getTime().getYear()+ 1900) + " по " + dateTo.getTime().getDate() + " / " + dateTo.getTime().getMonth() + " / " + (dateTo.getTime().getYear()+ 1900);
                            text_view_report_info.setText(text);
                        }
                    };
                    DatePickerDialog datePickerDialog;
                    datePickerDialog = new DatePickerDialog(this,
                            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                            dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), Calendar.DAY_OF_MONTH);

                    datePickerDialog.show();
                }
        );

        button_date_to.setOnClickListener(
                v -> {
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            dateTo.set(year, monthOfYear + 1, dayOfMonth);
                            text_view_report_info.clearComposingText();
                            String text = "Отчет по количеству продаж у каждого аптекаря в период с " + dateFrom.getTime().getDate() + " / " + dateFrom.getTime().getMonth() + " / " + (dateFrom.getTime().getYear()+ 1900) + " по " + dateTo.getTime().getDate() + " / " + dateTo.getTime().getMonth() + " / " + (dateTo.getTime().getYear()+ 1900);
                            text_view_report_info.setText(text);
                        }
                    };
                    DatePickerDialog datePickerDialog;
                    datePickerDialog = new DatePickerDialog(this,
                            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                            dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), Calendar.DAY_OF_MONTH);

                    datePickerDialog.show();
                }
        );

        button_report.setOnClickListener(
                v -> {
                    SupplyLogic supplyLogic = new SupplyLogic(this);
                    MedicineLogic medicineLogic = new MedicineLogic(this);
                    StorageLogic storageLogic = new StorageLogic(this);
                    ManufacturerLogic manufacturerLogic = new ManufacturerLogic(this);

                    Report report = new Report(this, userId, manufacturerLogic, storageLogic, supplyLogic, medicineLogic, dateFrom.getTime(), dateTo.getTime());

                    try {
                        report.generatePdf();

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                        builder1.setMessage("Файл отчета создан");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Ок",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );


    }
}
