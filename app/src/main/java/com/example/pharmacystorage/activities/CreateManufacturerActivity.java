package com.example.pharmacystorage.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.ManufacturerLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.SaleLogic;
import com.example.pharmacystorage.database.logics.SaleMedicinesLogic;
import com.example.pharmacystorage.database.models.MedicineModel;
import com.example.pharmacystorage.database.models.SaleMedicinesModel;
import com.example.pharmacystorage.database.models.SaleModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleActivity extends AppCompatActivity {

    Button button_create_medicine;
    Button button_cancel;
    Button button_save;
    EditText edit_text_name;
    EditText edit_text_email;
    EditText edit_text_address;
    ManufacturerLogic logic;
    MedicineLogic logicMed;
    TableRow selectedRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mapMedicine = new HashMap<MedicineModel, Integer>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_manufacturer);

        int userId = getIntent().getExtras().getInt("userId");
        int id = getIntent().getExtras().getInt("id");

        logic = new ManufacturerLogic(this);
        logicMed = new MedicineLogic(this);

        button_create_medicine = findViewById(R.id.button_to_create_medicine_activity);
        button_save = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);
        edit_text_count = findViewById(R.id.edit_text_count);

        logicM.open();
        List<MedicineModel> spinnerArray =  new ArrayList<MedicineModel>();
        spinnerArray.addAll(logicM.getFullList());
        logicM.close();

        ArrayAdapter<MedicineModel> adapter = new ArrayAdapter<MedicineModel>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_medicine = (Spinner) findViewById(R.id.spinner_name);
        spinner_medicine.setAdapter(adapter);

        if (id != 0){
            logic.open();
            SaleModel model = logic.getElement(id);
            logic.close();
            logicSM.open();
            List<SaleMedicinesModel> listSM = logicSM.getFilteredList(id);
            logicSM.close();
            logicM.open();
            for (int i = 0; i < listSM.size(); i++){
                mapMedicine.put(logicM.getElement(listSM.get(i).getMedicineid()),listSM.get(i).getCount());
            }
            logicM.close();
            fillTable(Arrays.asList("Название", "Количество упаковок"));

            mapMedicine.entrySet();

            this.date = new Date(model.getDate());
        } else {
            this.date = new Date();
        }

        button_add.setOnClickListener(
                v -> { ;
                    mapMedicine.put(((MedicineModel)spinner_medicine.getSelectedItem()), Integer.parseInt(edit_text_count.getText().toString()));
                    fillTable(Arrays.asList("Название", "Количество упаковок"));
                }
        );

        button_save.setOnClickListener(
                v -> { ;

                    SaleModel model = new SaleModel(date.getTime(), userId, null);
                    logic.open();

                    if(id != 0){
                        model.setId(id);
                        logic.update(model);
                    } else {
                        logic.insert(model);
                    }
                    int saleId = logic.getFullList().get(logic.getFullList().size()-1).getId();
                    logicSM.open();
                    for(Map.Entry<MedicineModel, Integer> entry: mapMedicine.entrySet()){
                        MedicineModel key = entry.getKey();
                        Integer value = entry.getValue();
                        SaleMedicinesModel modelSM = new SaleMedicinesModel(saleId, key.getId(), value);
                        logicSM.insert(modelSM);
                    }
                    logicSM.close();
                    logic.close();
                    this.finish();
                }
        );

        button_cancel.setOnClickListener(
                v -> finish()
        );
    }

    void fillTable(List<String> titles) {

        TableLayout tableLayoutCustomers = findViewById(R.id.tableLayoutMed);

        tableLayoutCustomers.removeAllViews();

        TableRow tableRowTitles = new TableRow(this);

        for (String title : titles) {
            TextView textView = new TextView(this);

            textView.setTextSize(16);
            textView.setText(title);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setWidth( (int)(getWindowManager().getDefaultDisplay().getWidth() / 2.2));
            tableRowTitles.addView(textView);
        }

        tableRowTitles.setBackgroundColor(Color.parseColor("#FF6200EE"));
        tableLayoutCustomers.addView(tableRowTitles);


        for(Map.Entry<MedicineModel, Integer> entry: mapMedicine.entrySet()) {
            MedicineModel key = entry.getKey();
            Integer value = entry.getValue();



            TableRow tableRow = new TableRow(this);

            TextView textViewName = new TextView(this);
            textViewName.setText(key.getName());
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewCount = new TextView(this);
            textViewName.setHeight(100);
            textViewCount.setTextSize(16);
            textViewCount.setText(String.valueOf(value));
            textViewCount.setTextColor(Color.WHITE);
            textViewCount.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setVisibility(View.INVISIBLE);
            textViewId.setText(String.valueOf(key.getId()));

            tableRow.addView(textViewName);
            tableRow.addView(textViewCount);
            tableRow.addView(textViewId);

            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));

            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for(int i = 0; i < tableLayoutCustomers.getChildCount(); i++){
                    View view = tableLayoutCustomers.getChildAt(i);
                    if (view instanceof TableRow){
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }

                tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));
            });

            tableLayoutCustomers.addView(tableRow);
        }
    }
}