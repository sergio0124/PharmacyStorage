package com.example.pharmacystorage.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.PharmacyLogic;
import com.example.pharmacystorage.database.logics.SendingLogic;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.helper_models.ReadEmail;
import com.example.pharmacystorage.models.ManufacturerModel;
import com.example.pharmacystorage.models.PharmacyModel;
import com.example.pharmacystorage.models.RequestAmount;
import com.example.pharmacystorage.models.RequestModel;
import com.example.pharmacystorage.models.SendingAmount;
import com.example.pharmacystorage.models.SendingModel;
import com.example.pharmacystorage.models.StorageModel;
import com.google.gson.internal.LinkedTreeMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SendsActivity extends AppCompatActivity {

    TableRow selectedRow;
    SendingLogic logicS;
    StorageLogic logicStorage;
    PharmacyLogic logicP;
    MedicineLogic logicM;
    Button button_create_send;
    Button button_send_send;
    Button button_cancel;
    int userId;
    TableLayout tableLayoutSending;
    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    List<String> titles = Arrays.asList("Аптека", "Дата запроса");
    Map<String, List<RequestAmount>> stringListMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sends);

        userId = getIntent().getExtras().getInt("userId");

        tableLayoutSending = findViewById(R.id.tableLayoutSending);
        logicS = new SendingLogic(this);
        logicStorage = new StorageLogic(this);
        logicP = new PharmacyLogic(this);
        logicM = new MedicineLogic(this);
        button_create_send = findViewById(R.id.button_to_create_sending);
        button_send_send = findViewById(R.id.button_send_sending);
        button_cancel = findViewById(R.id.button_cancel);

        button_create_send.setOnClickListener(v -> {
            Intent intent = new Intent(SendsActivity.this, CreateSendActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("id", 0);
            startActivity(intent);
        });

        button_send_send.setOnClickListener(v -> {
            if (selectedRow == null) {
                Toast.makeText(this, "Выберете строчку",
                        Toast.LENGTH_LONG).show();
                return;
            }

            String child = ((TextView) selectedRow.getChildAt(2)).getText().toString();
            SendingModel model = new SendingModel();
            model.setId(Integer.parseInt(child));

            Intent intent = new Intent(SendsActivity.this, GetSendActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("sendingId", model.getId());
            startActivity(intent);
        });

        button_cancel.setOnClickListener(v -> {
            finish();
        });


        //Task to get and insert sendings]
        logicStorage.open();
        StorageModel storageModel = logicStorage.getElement(userId);
        String email = storageModel.getEmail();
        String pas = storageModel.getEmailPassword();
        logicStorage.close();
        loadRequests(email, pas);


        fillTable();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (stringListMap.size() > 0) {
                    LoadSendings();
                    timer.cancel();
                    runOnUiThread(() -> {
                        fillTable();
                        Toast.makeText(button_cancel.getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    });
                }

            }
        }, 1000, 1000);
    }


    void loadRequests(String email, String password) {
        ReadEmail readEmail = new ReadEmail(this, userId, password, email, stringListMap);
        readEmail.execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fillTable();
    }


    void LoadSendings() {
        logicP.open();
        logicS.open();
        logicM.open();

        stringListMap.entrySet().stream().forEach((entry) -> {

            SendingModel sendingModel = new SendingModel();
            PharmacyModel pharmacyModel = logicP.getElement(entry.getKey());
            sendingModel.setPharmacyName(pharmacyModel.getName());
            sendingModel.setDate(GregorianCalendar.getInstance());
            sendingModel.setPharmacyId(pharmacyModel.getId());
            sendingModel.setStorageId(userId);
            logicS.insert(sendingModel);

            int sendingId = logicS.getFullList().get(logicS.getFullList().size() - 1).getId();

            List<SendingAmount> sendingAmounts = new ArrayList<>();
            for (Object v : entry.getValue()
            ) {
                if (v instanceof LinkedTreeMap) {
                    LinkedTreeMap<String, Object> data = (LinkedTreeMap) v;
                    int medicineId = logicM.getMedicineByFullName((String) Objects.requireNonNull(data.get("Name"))).getId()  ;
                    RequestAmount v_data = new RequestAmount();
                    v_data.setName((String) data.get("Name"));
                    v_data.setQuantity(((Double) Objects.requireNonNull(data.get("Quantity"))).intValue());
                    v_data.setCost(((Double) Objects.requireNonNull(data.get("Cost"))).intValue());
                    v_data.setMedicineId(medicineId);
                    SendingAmount tmpSending = new SendingAmount(v_data);
                    tmpSending.setSendingId(sendingId);
                    sendingAmounts.add(tmpSending);
                }
            }
            logicS.insertSendingAmounts(sendingAmounts);
        });

        logicS.close();
        logicM.close();
        logicP.close();

    }




    void fillTable() {
        logicS.open();
        List<SendingModel> sendingModels = logicS.getFilteredByUserIdList(userId);
        logicS.close();
        tableLayoutSending.removeAllViews();

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
        tableLayoutSending.addView(tableRowTitles);


        for (SendingModel sendingModel : sendingModels) {
            TableRow tableRow = new TableRow(this);

            TextView textViewName = new TextView(this);
            sdf.setTimeZone(sendingModel.getDate().getTimeZone());
            textViewName.setText(sdf.format(sendingModel.getDate().getTime()));
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewEmail = new TextView(this);
            textViewName.setHeight(100);
            textViewEmail.setTextSize(16);
            textViewEmail.setText(sendingModel.getPharmacyName());
            textViewEmail.setTextColor(Color.WHITE);
            textViewEmail.setGravity(Gravity.CENTER);

            TextView textViewId = new TextView(this);
            textViewId.setVisibility(View.INVISIBLE);
            textViewId.setText(String.valueOf(sendingModel.getId()));

            tableRow.addView(textViewEmail);
            tableRow.addView(textViewName);
            tableRow.addView(textViewId);
            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));

            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for (int i = 0; i < tableLayoutSending.getChildCount(); i++) {
                    View view = tableLayoutSending.getChildAt(i);
                    if (view instanceof TableRow) {
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }
                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));


            });

            tableRow.setOnLongClickListener(v -> {

                selectedRow = tableRow;

                for(int i = 0; i < tableLayoutSending.getChildCount(); i++){
                    View view = tableLayoutSending.getChildAt(i);
                    if (view instanceof TableRow){
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }
                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));

                String child = ((TextView) selectedRow.getChildAt(2)).getText().toString();
                SendingModel model = new SendingModel();
                model.setId(Integer.parseInt(child));

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Удалить запись?");
                builder.setNegativeButton("Отмена", (dialog, id) -> dialog.cancel());

                builder.setPositiveButton("Да",
                        (dialog, which) -> {
                            logicS.open();
                            logicS.delete(Integer.parseInt(child));

                            fillTable();
                            dialog.dismiss();
                            logicS.close();
                        }).create();

                AlertDialog alert = builder.create();
                alert.show();
                return false;
            });

            tableLayoutSending.addView(tableRow);
        }
    }
}