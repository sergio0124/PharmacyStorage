package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.RequestLogic;
import com.example.pharmacystorage.database.logics.SupplyLogic;

public class GetSupplyActivity extends AppCompatActivity {

    RequestLogic logicR;
    SupplyLogic logicS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_supply);
    }
}