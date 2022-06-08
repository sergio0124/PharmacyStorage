package com.example.pharmacystorage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;

public class MainMenuActivity extends AppCompatActivity {

    Button button_pharmacy;
    Button button_manufacturer;
    Button button_to_medicines_activity;
    Button button_to_basket;
    Button button_to_report_activity;
    Button button_to_edit_storage_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        int userId = getIntent().getExtras().getInt("userId");

        button_pharmacy = findViewById(R.id.button_to_pharmacy_activity);
        button_manufacturer = findViewById(R.id.button_to_manufacturer_activity);
        button_to_medicines_activity = findViewById(R.id.button_to_medicines_activity);
        button_to_basket = findViewById(R.id.button_to_basket_activity);
        button_to_report_activity = findViewById(R.id.button_to_report_activity);
        button_to_edit_storage_activity = findViewById(R.id.button_to_edit_storage_activity);

        button_pharmacy.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainMenuActivity.this, ClientActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
        );

        button_manufacturer.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainMenuActivity.this, ManufacturerActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
        );

        button_to_medicines_activity.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainMenuActivity.this, MedicineListActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
        );

        button_to_basket.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainMenuActivity.this, ShowBasketActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
        );

        button_to_report_activity.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainMenuActivity.this, ReportActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
        );

        button_to_edit_storage_activity.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainMenuActivity.this, RegisterActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
        );
    }

}