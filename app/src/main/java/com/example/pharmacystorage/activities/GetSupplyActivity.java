package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.pharmacystorage.database.logics.SupplyLogic;
import com.example.pharmacystorage.models.RequestAmount;
import com.example.pharmacystorage.models.RequestModel;
import com.example.pharmacystorage.models.SupplyAmount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetSupplyActivity extends AppCompatActivity {

    RequestLogic logicR;
    SupplyLogic logicS;
    Button acceptSupply;
    Button cancelButton;
    TableRow selectedRow;
    TableLayout tableLayoutSupplies;
    List<String> titles = Arrays.asList("Статус", "Медикамент", "Количество", "Стоимость");

    int RequestId;
    List<RequestAmount> requestAmounts = new ArrayList<>();
    List<SupplyAmount> supplyAmounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_supply);

        acceptSupply = findViewById(R.id.button_to_accept_supply);
        acceptSupply.setOnClickListener(v->{

        }
        );
        cancelButton = findViewById(R.id.button_cancel);
        tableLayoutSupplies = findViewById(R.id.tableLayoutSupply);

        RequestId = getIntent().getExtras().getInt("requestId");
        logicR = new RequestLogic(this);
        requestAmounts = logicR.getRequestAmountsById(RequestId);
        
        requestAmounts.stream().forEach(v->{
            supplyAmounts.add(new SupplyAmount(v.getMedicineId(), v.getName(), v.getQuantity(), v.getCost(), "Ожидание"));
        });
        
        LoadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData();
    }
    
    private void LoadData(){
        fillTable();
    }

    void fillTable() {
        tableLayoutSupplies.removeAllViews();

        TableRow tableRowTitles = new TableRow(this);

        for (String title : titles) {
            TextView textView = new TextView(this);

            textView.setTextSize(16);
            textView.setText(title);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setWidth( (int)(getWindowManager().getDefaultDisplay().getWidth() / (titles.size() + 0.2)));
            tableRowTitles.addView(textView);
        }

        tableRowTitles.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        tableLayoutSupplies.addView(tableRowTitles);


        for (SupplyAmount supplyAmount : supplyAmounts) {
            TableRow tableRow = new TableRow(this);

            TextView textViewStatus = new TextView(this);
            textViewStatus.setHeight(100);
            textViewStatus.setTextSize(16);
            textViewStatus.setText(supplyAmount.getState());
            textViewStatus.setTextColor(Color.WHITE);
            textViewStatus.setGravity(Gravity.CENTER);

            TextView textViewName = new TextView(this);
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setText(supplyAmount.getName());
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewQuantity = new TextView(this);
            textViewQuantity.setText(supplyAmount.getQuantity());
            textViewQuantity.setHeight(100);
            textViewQuantity.setTextSize(16);
            textViewQuantity.setTextColor(Color.WHITE);
            textViewQuantity.setGravity(Gravity.CENTER);

            TextView textViewCost = new TextView(this);
            textViewCost.setHeight(100);
            textViewCost.setTextSize(16);
            textViewCost.setText(supplyAmount.getCost());
            textViewCost.setTextColor(Color.WHITE);
            textViewCost.setGravity(Gravity.CENTER);

            tableRow.addView(textViewStatus);
            tableRow.addView(textViewName);
            tableRow.addView(textViewQuantity);
            tableRow.addView(textViewCost);

            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));

            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for(int i = 0; i < tableLayoutSupplies.getChildCount(); i++){
                    View view = tableLayoutSupplies.getChildAt(i);
                    if (view instanceof TableRow){
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }
                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));
            });

            tableLayoutSupplies.addView(tableRow);
        }
    }
}