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
import com.example.pharmacystorage.database.logics.BasketLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.PharmacyLogic;
import com.example.pharmacystorage.database.logics.SendingLogic;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.database.logics.SupplyLogic;
import com.example.pharmacystorage.helper_models.JSONHelper;
import com.example.pharmacystorage.helper_models.JavaMailApi;
import com.example.pharmacystorage.models.PharmacyModel;
import com.example.pharmacystorage.models.SendingAmount;
import com.example.pharmacystorage.models.SendingModel;
import com.example.pharmacystorage.models.StorageModel;
import com.example.pharmacystorage.models.SupplyAmount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GetSendActivity extends AppCompatActivity {

    SendingLogic logicS;
    MedicineLogic logicM;
    PharmacyLogic logicP;
    BasketLogic logicB;
    SupplyLogic logicSupply;
    StorageLogic logicStorage;
    Button acceptSupply;
    Button cancelButton;
    TableRow selectedRow;
    TableLayout tableLayoutSupplies;
    List<String> titles = Arrays.asList("Статус", "Медикамент", "Количество", "Стоимость");
    ActivityResultLauncher<Intent> mStartForResult;
    final private String LETTER_SUBJECT = "Sending Report";

    int sendingId;
    int userId;
    List<SendingAmount> sendingAmountAtStart = new ArrayList<>();
    List<SendingAmount> sendingAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_send);

        sendingAmount = new ArrayList<>();
        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    if (intent == null) return;
                    Bundle arguments = intent.getExtras();
                    SendingAmount model = (SendingAmount) arguments.getSerializable(SendingAmount.class.getSimpleName());
                    for (int i = 0; i < sendingAmount.size(); i++) {
                        if (model.getMedicineId() == (sendingAmount.get(i)).getMedicineId() && (sendingAmount.get(i)).getMedicineId() != 0) {
                            sendingAmount.set(i, model);
                            fillTable();
                            return;
                        }
                    }
                    sendingAmount.add(model);
                    fillTable();
                });

        acceptSupply = findViewById(R.id.button_to_accept_supply);
        acceptSupply.setOnClickListener(v -> {

            SaveSending();
            SendMessage();
            finish();
        });
        cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> {
            finish();
        });
        tableLayoutSupplies = findViewById(R.id.tableLayoutSupply);

        sendingId = getIntent().getExtras().getInt("sendingId");
        userId = getIntent().getExtras().getInt("userId");
        logicS = new SendingLogic(this);
        logicM = new MedicineLogic(this);
        logicB = new BasketLogic(this);
        logicSupply = new SupplyLogic(this);
        logicStorage = new StorageLogic(this);
        logicP = new PharmacyLogic(this);

        logicS.open();
        sendingAmountAtStart = logicS.getSendingAmountsById(sendingId);
        sendingAmountAtStart.forEach(v -> {
            SendingAmount sending = new SendingAmount(v.getMedicineId(), v.getSendingId(), v.getQuantity(), v.getCost(), v.getName(), "Ожидание");
            sending.setId(v.getId());
            sendingAmount.add(sending);
        });
        logicS.close();

        LoadData();
    }


    private void SendMessage() {

        logicS.open();
        logicP.open();

        SendingModel item = logicS.getElement(sendingId);
        PharmacyModel pharmacyModel = logicP.getElement(item.getPharmacyId());
        String Email = pharmacyModel.getEmail();

        StorageModel storageModel = logicStorage.getElement(userId);
        String sEmail = storageModel.getEmail();
        String sPassword = storageModel.getEmailPassword();

        JSONHelper<SendingAmount> jsonHelper = new JSONHelper<>();
        jsonHelper.exportToJSON(this, sendingAmount);
        String path = this.getFileStreamPath(jsonHelper.getPath()).getAbsolutePath();

        logicS.close();
        logicP.close();

        JavaMailApi javaMailAPI = new JavaMailApi(this, Email, LETTER_SUBJECT, "", sEmail, sPassword, path);
        javaMailAPI.execute();
    }


    private void SaveSending() {
        logicS.open();
        logicB.open();
        logicSupply.open();

        SendingModel model = logicS.getElement(sendingId);
        model.setSent(1);
        logicS.update(model);
        logicS.close();

        List<SendingAmount> listSA = sendingAmount;

        List<SupplyAmount> supplyAmounts = logicSupply.getSupplyAmountsByStorage(userId);
        listSA.forEach(v -> {
            int count = v.getQuantity();

            int i = 0;
            List<SupplyAmount> supplyAmountsByMedicine = supplyAmounts.stream().filter(rec -> rec.getMedicineId() == v.getMedicineId()).collect(Collectors.toList());
            while (count > 0 && (supplyAmountsByMedicine.size() - 1 - i)>-1) {
                SupplyAmount supplyAmount = supplyAmountsByMedicine.get(supplyAmountsByMedicine.size() - 1 - i);
                supplyAmount.setOldQuantity(supplyAmount.getQuantity());
                if (supplyAmount.getQuantity() > count) {
                    supplyAmount.setQuantity(supplyAmount.getQuantity() - count);
                    count = 0;
                } else {
                    supplyAmount.setQuantity(0);
                    count = count - supplyAmount.getQuantity();
                }
                logicSupply.updateSupplyAmount(supplyAmount);
                i++;
            }

        });

        listSA.stream().filter(v -> v.getStatus().contains("Недостача")).forEach(v -> logicB.insertMedicineById(v.getMedicineId(), userId));
        listSA.forEach(v -> v.setSendingId(sendingId));
        logicM.open();
        listSA.forEach(v -> v.setMedicineId(logicM.getMedicineByFullName(v.getName()).getId()));
        logicM.close();

        logicS.open();
        logicS.insertSendingAmounts(listSA);
        logicS.close();
        
        logicB.close();
        logicSupply.close();
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


        for (SendingAmount SendingAmount : sendingAmount) {
            TableRow tableRow = new TableRow(this);

            TextView textViewStatus = new TextView(this);
            textViewStatus.setHeight(100);
            textViewStatus.setTextSize(16);
            textViewStatus.setText(SendingAmount.getStatus());
            if (SendingAmount.getStatus().contains("Подтверждено")) {
                textViewStatus.setTextColor(Color.WHITE);
                textViewStatus.setBackgroundColor(Color.GREEN);
            } else if (SendingAmount.getStatus().contains("Недостача")) {
                textViewStatus.setBackgroundColor(Color.YELLOW);
                textViewStatus.setTextColor(Color.BLACK);
            } else if (SendingAmount.getStatus().contains("Брак")) {
                textViewStatus.setTextColor(Color.BLACK);
                textViewStatus.setBackgroundColor(Color.RED);
            }
            textViewStatus.setGravity(Gravity.CENTER);

            TextView textViewName = new TextView(this);
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setText(SendingAmount.getName());
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewQuantity = new TextView(this);
            textViewQuantity.setText(String.valueOf(SendingAmount.getQuantity()));
            textViewQuantity.setHeight(100);
            textViewQuantity.setTextSize(16);
            textViewQuantity.setTextColor(Color.WHITE);
            textViewQuantity.setGravity(Gravity.CENTER);

            TextView textViewCost = new TextView(this);
            textViewCost.setHeight(100);
            textViewCost.setTextSize(16);
            textViewCost.setText(String.valueOf(SendingAmount.getCost()));
            textViewCost.setTextColor(Color.WHITE);
            textViewCost.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setText(String.valueOf(SendingAmount.getId()));
            textViewId.setVisibility(View.INVISIBLE);

            tableRow.addView(textViewStatus);
            tableRow.addView(textViewName);
            tableRow.addView(textViewQuantity);
            tableRow.addView(textViewCost);
            tableRow.addView(textViewId);

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

                String child = ((TextView) selectedRow.getChildAt(4)).getText().toString();
                int id = Integer.parseInt(child);
                SendingAmount supplyAmo = sendingAmount.stream().filter(rec1 -> rec1.getId() == id).collect(Collectors.toList()).get(0);

                Intent intent = new Intent(GetSendActivity.this, CheckSendingActivity.class);
                intent.putExtra("SendingAmount", supplyAmo);
                intent.putExtra("userId", userId);
                mStartForResult.launch(intent);
                fillTable();

            });


            tableLayoutSupplies.addView(tableRow);
        }
    }


}
