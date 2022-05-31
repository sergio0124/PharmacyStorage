package com.example.pharmacystorage.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.MainActivity;
import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.models.StorageModel;

import java.util.Arrays;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    Button button_pharmacy;
    Button button_manufacturer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        button_pharmacy = findViewById(R.id.button_to_pharmacy_activity);
        button_manufacturer = findViewById(R.id.button_to_manufacturer_activity);

        button_pharmacy.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                    startActivity(intent);
                }
        );

        button_manufacturer.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MainMenuActivity.this, ManufacturerActivity.class);
                    startActivity(intent);
                }
        );
    }
}