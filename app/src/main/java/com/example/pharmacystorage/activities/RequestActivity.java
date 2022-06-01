package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.ManufacturerLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.models.ManufacturerModel;
import com.example.pharmacystorage.models.MedicineAmount;
import com.example.pharmacystorage.models.MedicineModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestActivity extends AppCompatActivity {

    TableRow selectedRow;
    Spinner spinner_manufacturer;
    Spinner spinner_medicine;
    ManufacturerLogic logic;
    MedicineLogic logicM;
    Date date;
    ArrayList<MedicineAmount> medicineAmounts = new ArrayList<>();
    List<String> titles = Arrays.asList("Наименование","Кол-во", "Цена шт.");
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        date = new Date();
        logic = new ManufacturerLogic(this);
        logicM = new MedicineLogic(this);
        spinner_manufacturer = findViewById(R.id.spinner_manufacturer_name);
        spinner_medicine = findViewById(R.id.spinner_medicine_name);

        userId = getIntent().getExtras().getInt("userId");
        LoadData();
    }

    private void LoadData() {
        logic.open();
        List<ManufacturerModel> spinnerArray = new ArrayList<ManufacturerModel>();
        spinnerArray.addAll(logic.getFilteredList(userId));
        logic.close();

        ArrayAdapter<ManufacturerModel> adapter = new ArrayAdapter<ManufacturerModel>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_manufacturer.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ManufacturerModel item = (ManufacturerModel) parent.getItemAtPosition(position);
                medicineAmounts.clear();
                fillTable();
                logicM.open();

                List<MedicineModel> spinnerArrayMeds = new ArrayList<>();
                spinnerArrayMeds.addAll(logicM.getFilteredList(item.getId()));
                ArrayAdapter<MedicineModel> adapterMeds = new ArrayAdapter<>(
                        RequestActivity.this, android.R.layout.simple_spinner_item, spinnerArrayMeds);
                adapterMeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_medicine.setAdapter(adapterMeds);

                logicM.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner_manufacturer.setOnItemSelectedListener(itemSelectedListener);


    }



    void fillTable() {

        TableLayout tableLayoutMedicines = findViewById(R.id.tableLayoutMed);

        tableLayoutMedicines.removeAllViews();

        TableRow tableRowTitles = new TableRow(this);

        for (String title : titles) {
            TextView textView = new TextView(this);

            textView.setTextSize(16);
            textView.setText(title);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setWidth( (int)(getWindowManager().getDefaultDisplay().getWidth() / 3.2));
            tableRowTitles.addView(textView);
        }

        tableRowTitles.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        tableLayoutMedicines.addView(tableRowTitles);


        for (MedicineAmount amount : medicineAmounts) {
            TableRow tableRow = new TableRow(this);

            TextView textViewName = new TextView(this);
            textViewName.setText(amount.getName());
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewEmail = new TextView(this);
            textViewName.setHeight(100);
            textViewEmail.setTextSize(16);
            textViewEmail.setText(amount.getQuantity());
            textViewEmail.setTextColor(Color.WHITE);
            textViewEmail.setGravity(Gravity.CENTER);

            TextView textViewAddress = new TextView(this);
            textViewName.setHeight(100);
            textViewAddress.setTextSize(16);
            textViewAddress.setText(String.valueOf(amount.getCost()));
            textViewAddress.setTextColor(Color.WHITE);
            textViewAddress.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setVisibility(View.INVISIBLE);
            textViewId.setText(String.valueOf(amount.getId()));

            tableRow.addView(textViewName);
            tableRow.addView(textViewEmail);
            tableRow.addView(textViewAddress);
            tableRow.addView(textViewId);

            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));

            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for(int i = 0; i < tableLayoutMedicines.getChildCount(); i++){
                    View view = tableLayoutMedicines.getChildAt(i);
                    if (view instanceof TableRow){
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }

                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));
            });

            tableLayoutMedicines.addView(tableRow);
        }
    }

}