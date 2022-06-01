package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.ManufacturerLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.helper_models.JavaMailApi;
import com.example.pharmacystorage.helper_models.JavaMailApi2;
import com.example.pharmacystorage.models.ManufacturerModel;
import com.example.pharmacystorage.models.MedicineAmount;
import com.example.pharmacystorage.models.MedicineModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

public class RequestActivity extends AppCompatActivity {

    TableRow selectedRow;
    Spinner spinner_manufacturer;
    Spinner spinner_medicine;
    ManufacturerLogic logic;
    MedicineLogic logicM;
    Date date;
    ArrayList<MedicineAmount> medicineAmounts = new ArrayList<>();
    List<String> titles = Arrays.asList("Наименование", "Кол-во", "Цена шт.");
    int userId;
    Button button_add;
    Button button_send;
    Button button_cancel;
    EditText edit_count;
    EditText edit_cost;

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

        button_add = findViewById(R.id.button_add);
        button_send = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);
        edit_cost = findViewById(R.id.edit_text_cost);
        edit_count = findViewById(R.id.edit_text_count);

        button_add.setOnClickListener(v -> {
            MedicineModel item =
                    (MedicineModel) spinner_medicine.getItemAtPosition(spinner_medicine.getSelectedItemPosition());
            String Name = item.toString();
            int count = Integer.parseInt(edit_count.getText().toString());
            int cost = Integer.parseInt(edit_cost.getText().toString());
            MedicineAmount amount = new MedicineAmount();
            amount.setMedicineId(item.getId());
            amount.setCost(cost);
            amount.setName(Name);
            amount.setQuantity(count);
            medicineAmounts.add(amount);
            fillTable();
        });

        button_send.setOnClickListener(v -> {
            try {
                SendMessage();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(RequestActivity.this, ManufacturerActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        button_cancel.setOnClickListener(v -> {
            finish();
        });

        LoadData();
    }

    private void SendMessage() throws MessagingException {
        //"wengarelo@mail.ru"
        ManufacturerModel item = (ManufacturerModel) spinner_manufacturer.getItemAtPosition(spinner_manufacturer.getSelectedItemPosition());
        String Email = item.getEmail();

        String subject = "subject";
        String message = "message";
    //    JavaMailApi javaMailAPI = new JavaMailApi(this,Email,subject,message);
        JavaMailApi2 jma = new JavaMailApi2();
        jma.message();

    //    javaMailAPI.execute();
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
            textView.setWidth((int) (getWindowManager().getDefaultDisplay().getWidth() / 3.2));
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
            textViewEmail.setText(String.valueOf(amount.getQuantity()));
            textViewEmail.setTextColor(Color.WHITE);
            textViewEmail.setGravity(Gravity.CENTER);

            TextView textViewAddress = new TextView(this);
            textViewName.setHeight(100);
            textViewAddress.setTextSize(16);
            textViewAddress.setText(String.valueOf(amount.getCost()));
            textViewAddress.setTextColor(Color.WHITE);
            textViewAddress.setGravity(Gravity.CENTER);

            tableRow.addView(textViewName);
            tableRow.addView(textViewEmail);
            tableRow.addView(textViewAddress);

            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));

            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for (int i = 0; i < tableLayoutMedicines.getChildCount(); i++) {
                    View view = tableLayoutMedicines.getChildAt(i);
                    if (view instanceof TableRow) {
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }

                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));
            });

            tableLayoutMedicines.addView(tableRow);
        }
    }

}