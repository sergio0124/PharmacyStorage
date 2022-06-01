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
import com.example.pharmacystorage.database.logics.RequestLogic;
import com.example.pharmacystorage.models.RequestModel;

import java.util.Arrays;
import java.util.List;

public class RequestActivity extends AppCompatActivity {

    TableRow selectedRow;
    RequestLogic logic;
    Button button_create_request;
    Button button_get_supply;
    int userId;
    TableLayout tableLayoutRequest;
    List<String> titles = Arrays.asList("Дата", "Предприятие");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        userId = getIntent().getExtras().getInt("userId");

        tableLayoutRequest = findViewById(R.id.tableLayoutRequest);
        logic = new RequestLogic(this);
        button_create_request = findViewById(R.id.button_to_create_request_activity);
        button_get_supply = findViewById(R.id.button_get_supply);

        button_create_request.setOnClickListener(v->{
            Intent intent = new Intent(RequestActivity.this, CreateRequestActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("id", 0);
            startActivity(intent);
        });

        button_get_supply.setOnClickListener(v->{

        });

        fillTable();
    }

    void fillTable() {
        List<RequestModel> requestModels = logic.getFilteredList(userId);
        tableLayoutRequest.removeAllViews();

        TableRow tableRowTitles = new TableRow(this);

        for (String title : titles) {
            TextView textView = new TextView(this);

            textView.setTextSize(16);
            textView.setText(title);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setWidth( (int)(getWindowManager().getDefaultDisplay().getWidth() / 2.2));
            tableRowTitles.addView(textView);
        }

        tableRowTitles.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        tableLayoutRequest.addView(tableRowTitles);


        for (RequestModel requestModel : requestModels) {
            TableRow tableRow = new TableRow(this);

            TextView textViewName = new TextView(this);
            textViewName.setText(requestModel.getDate().toString());
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewEmail = new TextView(this);
            textViewName.setHeight(100);
            textViewEmail.setTextSize(16);
            textViewEmail.setText(requestModel.getManufacturerName());
            textViewEmail.setTextColor(Color.WHITE);
            textViewEmail.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setVisibility(View.INVISIBLE);
            textViewId.setText(String.valueOf(requestModel.getId()));

            tableRow.addView(textViewName);
            tableRow.addView(textViewEmail);
            tableRow.addView(textViewId);

            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));

            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for(int i = 0; i < tableLayoutRequest.getChildCount(); i++){
                    View view = tableLayoutRequest.getChildAt(i);
                    if (view instanceof TableRow){
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }
                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));

                String child = ((TextView) selectedRow.getChildAt(2)).getText().toString();
                RequestModel model = new RequestModel();
                model.setId(Integer.parseInt(child));

                logic.open();

                model = logic.getElement(model.getId());
                Intent intent = new Intent(RequestActivity.this, CreateRequestActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("id", model.getId());
                startActivity(intent);

                logic.close();

            });

            tableLayoutRequest.addView(tableRow);
        }
    }
}