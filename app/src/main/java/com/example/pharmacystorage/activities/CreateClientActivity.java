package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.PharmacyLogic;
import com.example.pharmacystorage.models.PharmacyModel;

import java.util.Arrays;
import java.util.List;

public class CreateClientActivity extends AppCompatActivity {

    TableRow selectedRow;
    Button button_create_pharmacy;
    Button button_cancel;
    PharmacyLogic logic;
    int userId;
    int id;
    EditText edit_text_name;
    EditText edit_text_email;
    EditText edit_text_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_client);
        userId = getIntent().getExtras().getInt("userId");

        button_create_pharmacy = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);

        logic = new PharmacyLogic(this);
        edit_text_name = findViewById(R.id.edit_text_name);
        edit_text_email = findViewById(R.id.edit_text_email);
        edit_text_address = findViewById(R.id.edit_text_address);

        button_create_pharmacy.setOnClickListener(
                v -> { ;

                    PharmacyModel model = new PharmacyModel(edit_text_name.getText().toString(), edit_text_email.getText().toString(),
                            edit_text_address.getText().toString(), userId);
                    logic.open();

                    if(id != 0){
                        model.setId(id);
                        logic.update(model);
                    } else {
                        logic.insert(model);
                    }
                    logic.close();
                    this.finish();
                }
        );

        button_cancel.setOnClickListener(
                v -> {
                    finish();
                }
        );

    }


}