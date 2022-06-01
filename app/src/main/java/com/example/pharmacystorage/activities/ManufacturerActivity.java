package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.pharmacystorage.MainActivity;
import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.ManufacturerLogic;
import com.example.pharmacystorage.models.ManufacturerModel;

import java.util.Arrays;
import java.util.List;

public class ManufacturerActivity extends AppCompatActivity {

    TableRow selectedRow;
    Button button_create_manufacturer;
    Button button_create_request;
    Button button_acceptance_supply;
    ManufacturerLogic logic;
    int userId;
    TableLayout tableLayoutMedicines;


    @Override
    public void onResume() {
        super.onResume();
        logic.open();
        fillTable(Arrays.asList("Название", "Почта", "Адрес"), logic.getFilteredList(userId));
        logic.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufacturer);
        userId = getIntent().getExtras().getInt("userId");
        tableLayoutMedicines = findViewById(R.id.tableLayoutMedicines);

        button_create_manufacturer = findViewById(R.id.button_to_create_manufacturer_activity);
        button_create_request = findViewById(R.id.button_to_create_request_activity);
        button_acceptance_supply = findViewById(R.id.button_to_acceptance_supply_activity);

        logic = new ManufacturerLogic(this);

        button_create_manufacturer.setOnClickListener(
                v -> {
                    Intent intent = new Intent(ManufacturerActivity.this, CreateManufacturerActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("id", 0);
                    startActivity(intent);
                }
        );

        button_create_request.setOnClickListener(
                v -> {
                    Intent intent = new Intent(ManufacturerActivity.this, CreateRequestActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
        );

        button_acceptance_supply.setOnClickListener(
                v -> {
                    Intent intent = new Intent(ManufacturerActivity.this, MainActivity.class);
                    startActivity(intent);
                }
        );

        logic.open();
        fillTable(Arrays.asList("Название", "Почта", "Адрес"), logic.getFilteredList(userId));
        logic.close();

    }

    void fillTable(List<String> titles, List<ManufacturerModel> manufacturers) {



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


        for (ManufacturerModel manufacturer : manufacturers) {
            TableRow tableRow = new TableRow(this);

            TextView textViewName = new TextView(this);
            textViewName.setText(manufacturer.getName());
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewEmail = new TextView(this);
            textViewName.setHeight(100);
            textViewEmail.setTextSize(16);
            textViewEmail.setText(manufacturer.getEmail());
            textViewEmail.setTextColor(Color.WHITE);
            textViewEmail.setGravity(Gravity.CENTER);

            TextView textViewAddress = new TextView(this);
            textViewName.setHeight(100);
            textViewAddress.setTextSize(16);
            textViewAddress.setText(String.valueOf(manufacturer.getAddress()));
            textViewAddress.setTextColor(Color.WHITE);
            textViewAddress.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setVisibility(View.INVISIBLE);
            textViewId.setText(String.valueOf(manufacturer.getId()));

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

                String child = ((TextView) selectedRow.getChildAt(3)).getText().toString();
                ManufacturerModel model = new ManufacturerModel();
                model.setId(Integer.parseInt(child));

                logic.open();

                model = logic.getElement(model.getId());
                Intent intent = new Intent(ManufacturerActivity.this, CreateManufacturerActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("id", model.getId());
                intent.putExtra("name", model.getName());
                intent.putExtra("email", model.getEmail());
                intent.putExtra("address", model.getAddress());
                startActivity(intent);

                logic.close();

            });

            tableLayoutMedicines.addView(tableRow);
        }
    }
}