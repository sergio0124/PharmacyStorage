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

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.PharmacyLogic;
import com.example.pharmacystorage.models.MedicineModel;
import com.example.pharmacystorage.models.PharmacyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowBasketActivity extends AppCompatActivity {

    TableRow selectedRow;
    Button button_cancel;
    MedicineLogic logic;
    int userId;
    List<String> titles = new ArrayList<>(Arrays.asList("Полное название"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_basket);
        final int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(v -> {
            Intent intent = new Intent(ShowBasketActivity.this, MainMenuActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        button_cancel = findViewById(R.id.button_cancel);
        logic = new MedicineLogic(this);

        button_cancel.setOnClickListener(
                v -> {
                    finish();
                }
        );

        logic.open();
        fillTable(titles, logic.getFilteredListWithQuantityByStorage(userId));
        logic.close();
    }

    void fillTable(List<String> titles, List<MedicineModel> clients) {

        TableLayout tableLayoutClients = findViewById(R.id.tableLayoutMedicines);

        tableLayoutClients.removeAllViews();

        TableRow tableRowTitles = new TableRow(this);

        for (String title : titles) {
            TextView textView = new TextView(this);

            textView.setTextSize(16);
            textView.setText(title);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setWidth( (int)(getWindowManager().getDefaultDisplay().getWidth() / (1.2)));
            tableRowTitles.addView(textView);
        }

        tableRowTitles.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        tableLayoutClients.addView(tableRowTitles);


        for (MedicineModel Client : clients) {
            TableRow tableRow = new TableRow(this);

            TextView textViewName = new TextView(this);
            textViewName.setText(Client.toString());
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setVisibility(View.INVISIBLE);
            textViewId.setText(String.valueOf(Client.getId()));

            tableRow.addView(textViewName);
            tableRow.addView(textViewId);

            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));

            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for(int i = 0; i < tableLayoutClients.getChildCount(); i++){
                    View view = tableLayoutClients.getChildAt(i);
                    if (view instanceof TableRow){
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }

                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));
            });

            tableLayoutClients.addView(tableRow);
        }
    }
}