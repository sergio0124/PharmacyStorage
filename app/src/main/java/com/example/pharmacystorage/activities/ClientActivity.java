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
import com.example.pharmacystorage.database.logics.PharmacyLogic;
import com.example.pharmacystorage.helper_models.ReadEmail;
import com.example.pharmacystorage.models.PharmacyModel;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;

public class ClientActivity extends AppCompatActivity {

    TableRow selectedRow;
    Button button_create_pharmacy;
    Button button_sending;
    PharmacyLogic logic;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        userId = getIntent().getExtras().getInt("userId");

        button_create_pharmacy = findViewById(R.id.button_to_create_pharmacy_activity);
        button_sending = findViewById(R.id.button_to_sending);

        logic = new PharmacyLogic(this);

        button_create_pharmacy.setOnClickListener(
                v -> {
                    Intent intent = new Intent(ClientActivity.this, CreateClientActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("id", 0);
                    startActivity(intent);
                }
        );

        logic.open();
        fillTable(Arrays.asList("Название", "Почта", "Адрес"), logic.getFilteredList(userId));
        logic.close();

    }

    @Override
    public void onResume() {
        super.onResume();
        logic.open();
        fillTable(Arrays.asList("Название", "Почта", "Адрес"), logic.getFilteredList(userId));
        logic.close();
    }

    void fillTable(List<String> titles, List<PharmacyModel> clients) {

        TableLayout tableLayoutClients = findViewById(R.id.tableLayoutMedicines);

        tableLayoutClients.removeAllViews();

        TableRow tableRowTitles = new TableRow(this);

        for (String title : titles) {
            TextView textView = new TextView(this);

            textView.setTextSize(16);
            textView.setText(title);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setWidth( (int)(getWindowManager().getDefaultDisplay().getWidth() / (3.2)));
            tableRowTitles.addView(textView);
        }

        tableRowTitles.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        tableLayoutClients.addView(tableRowTitles);


        for (PharmacyModel Client : clients) {
            TableRow tableRow = new TableRow(this);

            TextView textViewName = new TextView(this);
            textViewName.setText(Client.getName());
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewEmail = new TextView(this);
            textViewName.setHeight(100);
            textViewEmail.setTextSize(16);
            textViewEmail.setText(Client.getEmail());
            textViewEmail.setTextColor(Color.WHITE);
            textViewEmail.setGravity(Gravity.CENTER);

            TextView textViewAddress = new TextView(this);
            textViewName.setHeight(100);
            textViewAddress.setTextSize(16);
            textViewAddress.setText(String.valueOf(Client.getAddress()));
            textViewAddress.setTextColor(Color.WHITE);
            textViewAddress.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setVisibility(View.INVISIBLE);
            textViewId.setText(String.valueOf(Client.getId()));

            tableRow.addView(textViewName);
            tableRow.addView(textViewEmail);
            tableRow.addView(textViewAddress);
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