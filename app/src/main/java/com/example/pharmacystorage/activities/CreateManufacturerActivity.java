package com.example.pharmacystorage.activities;

import android.app.AlertDialog;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.ManufacturerLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.helper_models.Validators;
import com.example.pharmacystorage.models.ManufacturerModel;
import com.example.pharmacystorage.models.MedicineModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateManufacturerActivity extends AppCompatActivity {

    Button button_create_medicine;
    Button button_cancel;
    Button button_save;
    EditText edit_text_name;
    EditText edit_text_email;
    EditText edit_text_address;
    ManufacturerLogic logic;
    MedicineLogic logicMed;
    TableRow selectedRow;
    List<MedicineModel> medicines;
    int userId;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_manufacturer);

        userId = getIntent().getExtras().getInt("userId");
        id = getIntent().getExtras().getInt("id");

        logic = new ManufacturerLogic(this);
        logicMed = new MedicineLogic(this);

        button_create_medicine = findViewById(R.id.button_to_create_medicine_activity);
        button_save = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);
        edit_text_name = findViewById(R.id.edit_text_name);
        edit_text_email = findViewById(R.id.edit_text_email);
        edit_text_address = findViewById(R.id.edit_text_address);

        if(id != 0){
            edit_text_name.setText(getIntent().getExtras().getString("name"));
            edit_text_email.setText(getIntent().getExtras().getString("email"));
            edit_text_address.setText(getIntent().getExtras().getString("address"));
            logicMed.open();
            if (logicMed.getFilteredList(id).size() != 0){
                medicines = logicMed.getFilteredList(id);
            }
            logicMed.close();
            fillTable(Arrays.asList("Название", "Дозировка", "Форма выпуска"), medicines);
        }else {
            medicines = new ArrayList<>();
        }


        ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    Bundle arguments = intent.getExtras();
                    MedicineModel model = (MedicineModel)arguments.getSerializable(MedicineModel.class.getSimpleName());
                    medicines.add(model);
                    fillTable(Arrays.asList("Название", "Дозировка", "Форма выпуска"), medicines);
                });

        button_create_medicine.setOnClickListener(
                v -> { ;
                    Intent intent = new Intent(CreateManufacturerActivity.this, MedicineActivity.class);
                    intent.putExtra("id", 0);
                    mStartForResult.launch(intent);

                    fillTable(Arrays.asList("Название", "Дозировка", "Форма выпуска"), medicines);
                }
        );

        button_save.setOnClickListener(
                v -> { ;

                    ManufacturerModel model = new ManufacturerModel(edit_text_name.getText().toString(), edit_text_email.getText().toString(),
                            edit_text_address.getText().toString(), userId);

                    if (!Validators.validateEmail(model.getEmail())){
                        errorDialog("Неверный формат почты");
                        return;
                    }

                    logic.open();

                    for (ManufacturerModel mod : logic.getFullList()){
                        if (mod.getEmail().equals(model.getEmail()) && model.getId() != mod.getId()){
                            errorDialog("Такая почта уже есть");
                        }
                    }

                    int manufactureId;
                    if(id != 0){
                        model.setId(id);
                        logic.update(model);
                        manufactureId = id;
                    } else {
                        logic.insert(model);
                        manufactureId = logic.getFullList().get(logic.getFullList().size()-1).getId();
                    }

                    logicMed.open();
                    for(MedicineModel medicine: medicines){
                        if(medicine.getId() != 0){
                            logicMed.update(medicine);
                        }else {
                            medicine.setManufacturerId(manufactureId);
                            logicMed.insert(medicine);
                        }

                    }
                    logicMed.close();
                    logic.close();
                    this.finish();
                }
        );

        button_cancel.setOnClickListener(
                v -> finish()
        );
    }

    void fillTable(List<String> titles, List<MedicineModel> medicines) {

        TableLayout tableLayoutCustomers = findViewById(R.id.tableLayoutMed);

        tableLayoutCustomers.removeAllViews();

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

        tableRowTitles.setBackgroundColor(Color.parseColor("#FF6200EE"));
        tableLayoutCustomers.addView(tableRowTitles);


        for(MedicineModel medicine: medicines) {
            TableRow tableRow = new TableRow(this);

            TextView textViewName = new TextView(this);
            textViewName.setText(medicine.getName());
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewDosage = new TextView(this);
            textViewName.setHeight(100);
            textViewDosage.setTextSize(16);
            textViewDosage.setText(String.valueOf(medicine.getDosage()));
            textViewDosage.setTextColor(Color.WHITE);
            textViewDosage.setGravity(Gravity.CENTER);

            TextView textViewForm = new TextView(this);
            textViewName.setHeight(100);
            textViewForm.setTextSize(16);
            textViewForm.setText(String.valueOf(medicine.getForm()));
            textViewForm.setTextColor(Color.WHITE);
            textViewForm.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setVisibility(View.INVISIBLE);
            textViewId.setText(String.valueOf(medicine.getId()));

            tableRow.addView(textViewName);
            tableRow.addView(textViewDosage);
            tableRow.addView(textViewForm);
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

    private void errorDialog(String err){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateManufacturerActivity.this);
        builder.setMessage(err);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "ОК",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }
}