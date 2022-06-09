package com.example.pharmacystorage.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.ManufacturerLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.RequestLogic;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.database.logics.SupplyLogic;
import com.example.pharmacystorage.helper_models.JSONHelper;
import com.example.pharmacystorage.helper_models.JavaMailApi;
import com.example.pharmacystorage.models.ManufacturerModel;
import com.example.pharmacystorage.models.RequestAmount;
import com.example.pharmacystorage.models.RequestModel;
import com.example.pharmacystorage.models.StorageModel;
import com.example.pharmacystorage.models.SupplyAmount;
import com.example.pharmacystorage.models.SupplyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class GetSupplyActivity extends AppCompatActivity {

    RequestLogic logicR;
    SupplyLogic logicS;
    MedicineLogic logicM;
    ManufacturerLogic logicManufacturer;
    StorageLogic logicStorage;
    Button acceptSupply;
    Button cancelButton;
    TableRow selectedRow;
    TableLayout tableLayoutSupplies;
    List<String> titles = Arrays.asList("Статус", "Медикамент", "Количество", "Стоимость");
    ActivityResultLauncher<Intent> mStartForResult;
    final private String LETTER_SUBJECT = "Supply Report";

    int requestId;
    int userId;
    List<RequestAmount> requestAmounts = new ArrayList<>();
    List<SupplyAmount> supplyAmounts = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_supply);

        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    if (intent == null) return;
                    Bundle arguments = intent.getExtras();
                    SupplyAmount model = (SupplyAmount) arguments.getSerializable(SupplyAmount.class.getSimpleName());
                    for (int i = 0; i < supplyAmounts.size(); i++) {
                        if (model.getMedicineId() == ((SupplyAmount) supplyAmounts.get(i)).getMedicineId() && ((SupplyAmount) supplyAmounts.get(i)).getMedicineId() != 0) {
                            supplyAmounts.set(i, model);
                            fillTable();
                            return;
                        }
                    }
                    supplyAmounts.add(model);
                    fillTable();
                });

        acceptSupply = findViewById(R.id.button_to_accept_supply);
        acceptSupply.setOnClickListener(v -> {

            SaveSupply();
            SendMessage();
            DeleteRequest();
            finish();
        });
        cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> {
            finish();
        });
        tableLayoutSupplies = findViewById(R.id.tableLayoutSupply);

        requestId = getIntent().getExtras().getInt("requestId");
        userId = getIntent().getExtras().getInt("userId");
        logicR = new RequestLogic(this);
        logicS = new SupplyLogic(this);
        logicM = new MedicineLogic(this);
        logicStorage = new StorageLogic(this);
        logicManufacturer = new ManufacturerLogic(this);
        requestAmounts = logicR.getRequestAmountsById(requestId);

        requestAmounts.stream().forEach(v -> {
            supplyAmounts.add(new SupplyAmount(v.getMedicineId(), v.getName(), v.getQuantity(), v.getCost(), "Ожидание"));
        });

        LoadData();
    }

    private void SendMessage() {

        logicR.open();
        logicManufacturer.open();

        RequestModel item = (RequestModel) logicR.getElement(requestId);
        ManufacturerModel manufacturerModel = logicManufacturer.getElement(item.getManufacturerId());
        String Email = manufacturerModel.getEmail();

        StorageModel storageModel = logicStorage.getElement(userId);
        String sEmail = storageModel.getEmail();
        String sPassword = storageModel.getEmailPassword();

        JSONHelper<SupplyAmount> jsonHelper = new JSONHelper<>();
        jsonHelper.exportToJSON(this, supplyAmounts);
        String path = this.getFileStreamPath(jsonHelper.getPath()).getAbsolutePath();

        logicR.close();
        logicManufacturer.close();

        JavaMailApi javaMailAPI = new JavaMailApi(this, Email, LETTER_SUBJECT, "", sEmail, sPassword, path);
        javaMailAPI.execute();
    }


    private void SaveSupply() {
        logicS.open();
        logicM.open();

        SupplyModel model = new SupplyModel();
        model.setDate(Calendar.getInstance());
        model.setStorageId(userId);
        logicS.insert(model);

        SupplyModel supplyModel = logicS.getFilteredByStorageList(userId).get(logicS.getFilteredByStorageList(userId).size() - 1);
        List<SupplyAmount> listSA = supplyAmounts.stream().filter(v -> !v.getState().contains("Брак")).collect(Collectors.toList());
        listSA.stream().forEach(v -> v.setSupplyId(supplyModel.getId()));
        listSA.stream().forEach(v -> v.setMedicineId( logicM.getMedicineByFullName(v.getName()).getId()));

        logicS.insertSupplyAmounts(listSA);

        logicS.close();
        logicM.close();
    }


    private void DeleteRequest(){
        logicR.open();
        logicR.delete(requestId);

        logicR.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData();
    }




    private void LoadData() {
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
            textView.setWidth((int) (getWindowManager().getDefaultDisplay().getWidth() / (titles.size() + 0.2)));
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
            if (supplyAmount.getState().contains("Подтверждено")) {
                textViewStatus.setTextColor(Color.WHITE);
                textViewStatus.setBackgroundColor(Color.GREEN);
            } else if (supplyAmount.getState().contains("Недостача")) {
                textViewStatus.setBackgroundColor(Color.YELLOW);
                textViewStatus.setTextColor(Color.BLACK);
            } else if (supplyAmount.getState().contains("Брак")) {
                textViewStatus.setTextColor(Color.BLACK);
                textViewStatus.setBackgroundColor(Color.RED);
            }
            textViewStatus.setGravity(Gravity.CENTER);

            TextView textViewName = new TextView(this);
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setText(supplyAmount.getName());
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewQuantity = new TextView(this);
            textViewQuantity.setText(String.valueOf(supplyAmount.getQuantity()));
            textViewQuantity.setHeight(100);
            textViewQuantity.setTextSize(16);
            textViewQuantity.setTextColor(Color.WHITE);
            textViewQuantity.setGravity(Gravity.CENTER);

            TextView textViewCost = new TextView(this);
            textViewCost.setHeight(100);
            textViewCost.setTextSize(16);
            textViewCost.setText(String.valueOf(supplyAmount.getCost()));
            textViewCost.setTextColor(Color.WHITE);
            textViewCost.setGravity(Gravity.CENTER);

            tableRow.addView(textViewStatus);
            tableRow.addView(textViewName);
            tableRow.addView(textViewQuantity);
            tableRow.addView(textViewCost);

            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));


            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for (int i = 0; i < tableLayoutSupplies.getChildCount(); i++) {
                    View view = tableLayoutSupplies.getChildAt(i);
                    if (view instanceof TableRow) {
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }
                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));

                String child = ((TextView) selectedRow.getChildAt(1)).getText().toString();
                SupplyAmount supplyAmo = supplyAmounts.stream()
                        .filter(rec -> rec.getName().contains(child)).collect(Collectors.toList()).get(0);

                Intent intent = new Intent(GetSupplyActivity.this, CheckSupplyActivity.class);
                intent.putExtra("SupplyAmount", supplyAmo);
                mStartForResult.launch(intent);


                fillTable();

            });


            tableLayoutSupplies.addView(tableRow);
        }
    }
}