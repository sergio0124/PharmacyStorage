package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.EditText;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.models.MedicineModel;

import java.io.Serializable;

public class MedicineActivity extends AppCompatActivity {

    Button button_create;
    Button button_cancel;
    EditText edit_text_medicine_name;
    EditText edit_text_dosage;
    EditText edit_text_form;
    MedicineLogic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        int id = getIntent().getExtras().getInt("id");
        int manufacturerId = getIntent().getExtras().getInt("manufacturerId");

        logic = new MedicineLogic(this);

        button_create = findViewById(R.id.button_create);
        button_cancel = findViewById(R.id.button_cancel);
        edit_text_medicine_name = findViewById(R.id.edit_text_medicine_name);
        edit_text_dosage = findViewById(R.id.edit_text_dosage);
        edit_text_form = findViewById(R.id.edit_text_form);

        if (id != 0) {
            logic.open();
            MedicineModel model = logic.getElement(id);
            logic.close();

            edit_text_medicine_name.setText(model.getName());
            edit_text_dosage.setText(String.valueOf(model.getDosage()));
            edit_text_form.setText(String.valueOf(model.getForm()));
        }

        button_create.setOnClickListener(
                v -> {
                    MedicineModel model = new MedicineModel(
                            edit_text_medicine_name.getText().toString(),
                            Integer.parseInt(edit_text_dosage.getText().toString()),
                            edit_text_form.getText().toString());
                    logic.open();
                    if (manufacturerId != 0){
                        model.setManufacturerId(manufacturerId);
                    }

                    if (id != 0) {
                        model.setId(id);
                        logic.update(model);
                    } else {
                        Intent intent = new Intent(this, CreateManufacturerActivity.class);
                        intent.putExtra("MedicineModel", model);
                        setResult(RESULT_OK, intent);
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