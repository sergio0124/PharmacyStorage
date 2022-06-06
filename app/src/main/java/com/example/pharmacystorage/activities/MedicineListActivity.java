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
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.models.MedicineModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MedicineListActivity extends AppCompatActivity {

    Button searchButton;
    TableRow selectedRow;
    TableLayout tableLayoutRequest;
    EditText searchEditText;
    List<MedicineModel> medicineModels = new ArrayList<>();

    MedicineLogic logicM;
    int userId;

    List<String> titles = new ArrayList<>(Arrays.asList("Название", "Количество"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_list);

        searchButton = findViewById(R.id.button_search_content);
        searchEditText = findViewById(R.id.edit_search_content);
        tableLayoutRequest = findViewById(R.id.tableLayoutMedicines);
        logicM = new MedicineLogic(this);
        userId = getIntent().getExtras().getInt("userId");

        searchButton.setOnClickListener(v -> {
            fillTable();
        });

        fillTable();

    }


    void fillTable() {
        medicineModels = logicM.getFilteredListWithQuantityByStorage(userId);

        tableLayoutRequest.removeAllViews();

        filterMedicineList();

        TableRow tableRowTitles = new TableRow(this);

        for (String title : titles) {
            TextView textView = new TextView(this);

            textView.setTextSize(16);
            textView.setText(title);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setWidth((int) (getWindowManager().getDefaultDisplay().getWidth() / 2.2));
            tableRowTitles.addView(textView);
        }

        tableRowTitles.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        tableLayoutRequest.addView(tableRowTitles);


        for (MedicineModel medicineModel : medicineModels) {
            TableRow tableRow = new TableRow(this);

            TextView textViewFullName = new TextView(this);
            textViewFullName.setText(medicineModel.toString());
            textViewFullName.setHeight(100);
            textViewFullName.setTextSize(16);
            textViewFullName.setTextColor(Color.WHITE);
            textViewFullName.setGravity(Gravity.CENTER);

            TextView textViewEmail = new TextView(this);
            textViewFullName.setHeight(100);
            textViewEmail.setTextSize(16);
            textViewEmail.setText(String.valueOf(medicineModel.getQuantityOnStorage()));
            textViewEmail.setTextColor(Color.WHITE);
            textViewEmail.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setVisibility(View.INVISIBLE);
            textViewId.setText(String.valueOf(medicineModel.getId()));

            tableRow.addView(textViewFullName);
            tableRow.addView(textViewEmail);
            tableRow.addView(textViewId);

            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));

            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for (int i = 0; i < tableLayoutRequest.getChildCount(); i++) {
                    View view = tableLayoutRequest.getChildAt(i);
                    if (view instanceof TableRow) {
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }
                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));
            });

            tableLayoutRequest.addView(tableRow);
        }
    }

    private void filterMedicineList() {
        String searchLine = searchEditText.getText().toString();
        medicineModels = medicineModels.stream().filter(v-> v.toString().contains(searchLine)).collect(Collectors.toList());
    }
}