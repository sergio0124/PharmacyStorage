package com.example.pharmacystorage.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import com.example.pharmacystorage.R;
import com.example.pharmacystorage.Report;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        button_date_from = findViewById(R.id.button_date_from);
        button_date_to = findViewById(R.id.button_date_to);
        button_report = findViewById(R.id.button_report);

        text_view_report_info = findViewById(R.id.text_view_report_info);

        dateFrom.set(2021, 1, 1);
        dateTo.set(2021, 1, 1);

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
                            dateSetListener, 2021, 0, 1);

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
                            dateSetListener, 2021, 0, 1);

                    datePickerDialog.show();
                }
        );

        button_report.setOnClickListener(
                v -> {
                    Report report = new Report();
              //      SaleLogic saleLogic = new SaleLogic(this);
             //       UserLogic userLogic = new UserLogic(this);

               //     try {
                    //    report.generatePdf(saleLogic, userLogic, dateFrom.getTime(), dateTo.getTime());

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
                //    } catch (IOException e) {
               //         e.printStackTrace();
                //    }
                }
        );


    }
}