package com.example.pharmacystorage.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.BasketLogic;
import com.example.pharmacystorage.database.logics.ManufacturerLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.RequestLogic;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.helper_models.JSONHelper;
import com.example.pharmacystorage.helper_models.JavaMailApi;
import com.example.pharmacystorage.models.ManufacturerModel;
import com.example.pharmacystorage.models.MedicineModel;
import com.example.pharmacystorage.models.RequestAmount;
import com.example.pharmacystorage.models.RequestModel;
import com.example.pharmacystorage.models.StorageModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CreateRequestActivity extends AppCompatActivity {

    TableRow selectedRow;
    Spinner spinner_manufacturer;
    Spinner spinner_medicine;
    ManufacturerLogic logic;
    MedicineLogic logicM;
    StorageLogic logicS;
    RequestLogic logicR;
    Date date;
    ArrayList<RequestAmount> requestAmounts = new ArrayList<>();
    List<String> titles = Arrays.asList("Наименование", "Кол-во", "Цена шт.");
    int userId;
    int manufacturerId;
    int iid = 0;
    Button button_add;
    Button button_send;
    Button button_cancel;
    EditText edit_count;
    EditText edit_cost;
    List<ManufacturerModel> spinnerArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        date = new Date();
        logic = new ManufacturerLogic(this);
        logicM = new MedicineLogic(this);
        logicS = new StorageLogic(this);
        logicR = new RequestLogic(this);
        spinner_manufacturer = findViewById(R.id.spinner_manufacturer_name);
        spinner_medicine = findViewById(R.id.spinner_medicine_name);

        userId = getIntent().getExtras().getInt("userId");
        iid = getIntent().getExtras().getInt("id");
        manufacturerId = getIntent().getExtras().getInt("manufacturerId");

        button_add = findViewById(R.id.button_add);
        button_send = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);
        edit_cost = findViewById(R.id.edit_text_cost);
        edit_count = findViewById(R.id.edit_text_count);

        button_add.setOnClickListener(v -> {
            MedicineModel item =
                    (MedicineModel) spinner_medicine.getItemAtPosition(spinner_medicine.getSelectedItemPosition());
            String Name = item.toString();
            int count = Integer.parseInt(edit_count.getText().toString());
            int cost = Integer.parseInt(edit_cost.getText().toString());

            for (RequestAmount model : requestAmounts) {
                if (model.getMedicineId() == item.getId() && item.getId() != 0) {
                    model.setCost(cost);
                    model.setQuantity(count);
                    fillTable();
                    return;
                }
            }

            RequestAmount amount = new RequestAmount();
            amount.setMedicineId(item.getId());
            amount.setCost(cost);
            amount.setName(Name);
            amount.setQuantity(count);
            requestAmounts.add(amount);
            fillTable();
        });

        button_send.setOnClickListener(v -> {
            if (requestAmounts.size() > 0) {
                try {
                    SaveRequest();
                    SendMessage();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Intent intent = new Intent(CreateRequestActivity.this, ManufacturerActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });

        button_cancel.setOnClickListener(v -> finish());

        LoadData();
        if(manufacturerId>0){
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        LoadBasket();
                    });
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 500);
        }
    }

    private void SaveRequest() {
        logicR.open();

        ManufacturerModel item = (ManufacturerModel) spinner_manufacturer.getItemAtPosition(spinner_manufacturer.getSelectedItemPosition());
        RequestModel requestModel = new RequestModel();
        requestModel.setManufacturerId(item.getId());
        requestModel.setDate(Calendar.getInstance());
        requestModel.setStorageId(userId);
        logicR.insert(requestModel);

        int requestId = logicR.getFullList().get(logicR.getFullList().size() - 1).getId();
        requestAmounts.forEach(v -> v.setRequestId(requestId));

        logicR.insertRequestMedicines(requestAmounts);
        logicR.close();
    }

    private void SendMessage() {
        ManufacturerModel item = (ManufacturerModel) spinner_manufacturer.getItemAtPosition(spinner_manufacturer.getSelectedItemPosition());
        String Email = item.getEmail();

        StorageModel storageModel = logicS.getElement(userId);
        String sEmail = storageModel.getEmail();
        String sPassword = storageModel.getEmailPassword();

        JSONHelper<RequestAmount> jsonHelper = new JSONHelper<>();
        jsonHelper.exportToJSON(this, requestAmounts);
        String path = this.getFileStreamPath(jsonHelper.getPath()).getAbsolutePath();

        String LETTER_SUBJECT = "Request to medicines";
        JavaMailApi javaMailAPI = new JavaMailApi(this, Email, LETTER_SUBJECT, "", sEmail, sPassword, path);
        javaMailAPI.execute();
    }

    private void LoadData() {

        logic.open();
        spinnerArray = new ArrayList<>(logic.getFilteredList(userId));
        logic.close();

        ArrayAdapter<ManufacturerModel> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_manufacturer.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (iid == 0) {
                    ManufacturerModel item = (ManufacturerModel) parent.getItemAtPosition(position);
                    requestAmounts.clear();
                    fillTable();
                    logicM.open();

                    List<MedicineModel> spinnerArrayMeds = new ArrayList<>(logicM.getFilteredList(item.getId()));
                    ArrayAdapter<MedicineModel> adapterMeds = new ArrayAdapter<>(
                            CreateRequestActivity.this, android.R.layout.simple_spinner_item, spinnerArrayMeds);
                    adapterMeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_medicine.setAdapter(adapterMeds);

                    logicM.close();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner_manufacturer.setOnItemSelectedListener(itemSelectedListener);

        if (iid > 0) {
            button_add.setVisibility(View.INVISIBLE);
            button_send.setVisibility(View.INVISIBLE);
            logicR.open();
            logic.open();

            RequestModel requestModel = logicR.getElement(iid);
            ManufacturerModel manufacturerModel = logic.getElement(requestModel.getManufacturerId());
            ArrayAdapter<String> adapterNoChoose = new ArrayAdapter<>(
                    CreateRequestActivity.this, android.R.layout.simple_spinner_item, Collections.singletonList(manufacturerModel.getName()));
            spinner_manufacturer.setAdapter(adapterNoChoose);
            spinner_manufacturer.setEnabled(false);
            spinner_medicine.setAdapter(null);

            ArrayList<RequestAmount> amounts = logicR.getRequestAmountsById(iid);
            logicR.close();
            logic.close();
            requestAmounts = amounts;
            fillTable();
        }
    }

    void LoadBasket(){
        logic.open();
        ManufacturerModel manufacturerModel = logic.getElement(manufacturerId);
        logic.close();
        spinner_manufacturer.setSelection(spinnerArray.indexOf(manufacturerModel));

        List<MedicineModel> spinnerArrayMeds = new ArrayList<>(logicM.getFilteredList(manufacturerId));
        ArrayAdapter<MedicineModel> adapterMeds = new ArrayAdapter<>(
                CreateRequestActivity.this, android.R.layout.simple_spinner_item, spinnerArrayMeds);
        adapterMeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_medicine.setAdapter(adapterMeds);

        BasketLogic basketLogic = new BasketLogic(this);
        List<MedicineModel> medicineModels = basketLogic.getMedicinesByManufacturer(manufacturerId, userId);
        medicineModels.forEach(rec -> {
            RequestAmount requestAmount = new RequestAmount();
            requestAmount.setRequestId(0);
            requestAmount.setMedicineId(rec.getId());
            requestAmount.setName(rec.toString());
            requestAmount.setCost(1);
            requestAmount.setQuantity(1);

            requestAmounts.add(requestAmount);
        });

        fillTable();
    }

    void fillTable() {

        TableLayout tableLayoutMedicines = findViewById(R.id.tableLayoutMed);

        tableLayoutMedicines.removeAllViews();

        TableRow tableRowTitles = new TableRow(this);

        for (String title : titles) {
            TextView textView = new TextView(this);

            textView.setTextSize(16);
            textView.setText(title);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setWidth((int) (getWindowManager().getDefaultDisplay().getWidth() / 3.2));
            tableRowTitles.addView(textView);
        }

        tableRowTitles.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        tableLayoutMedicines.addView(tableRowTitles);


        for (RequestAmount amount : requestAmounts) {
            TableRow tableRow = new TableRow(this);

            TextView textViewName = new TextView(this);
            textViewName.setText(amount.getName());
            textViewName.setHeight(100);
            textViewName.setTextSize(16);
            textViewName.setTextColor(Color.WHITE);
            textViewName.setGravity(Gravity.CENTER);

            TextView textViewEmail = new TextView(this);
            textViewName.setHeight(100);
            textViewEmail.setTextSize(16);
            textViewEmail.setText(String.valueOf(amount.getQuantity()));
            textViewEmail.setTextColor(Color.WHITE);
            textViewEmail.setGravity(Gravity.CENTER);

            TextView textViewAddress = new TextView(this);
            textViewName.setHeight(100);
            textViewAddress.setTextSize(16);
            textViewAddress.setText(String.valueOf(amount.getCost()));
            textViewAddress.setTextColor(Color.WHITE);
            textViewAddress.setGravity(Gravity.CENTER);

            tableRow.addView(textViewName);
            tableRow.addView(textViewEmail);
            tableRow.addView(textViewAddress);

            tableRow.setBackgroundColor(Color.parseColor("#FF03DAC5"));

            tableRow.setOnClickListener(v -> {

                selectedRow = tableRow;

                for (int i = 0; i < tableLayoutMedicines.getChildCount(); i++) {
                    View view = tableLayoutMedicines.getChildAt(i);
                    if (view instanceof TableRow) {
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }

                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));
            });

            tableRow.setOnLongClickListener(v -> {
                if (iid != 0) {
                    Toast.makeText(this, "Нельзя удалять записи при просмотре", Toast.LENGTH_LONG).show();
                    return false;
                }

                selectedRow = tableRow;

                for (int i = 0; i < tableLayoutMedicines.getChildCount(); i++) {
                    View view = tableLayoutMedicines.getChildAt(i);
                    if (view instanceof TableRow) {
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }
                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));

                String fullNameField = ((TextView) selectedRow.getChildAt(0)).getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Удалить запись?");
                builder.setNegativeButton("Отмена", (dialog, id) -> dialog.cancel());

                builder.setPositiveButton("Да",
                        (dialog, which) -> {
                            requestAmounts.removeIf(rec -> rec.getName().contains(fullNameField));
                            fillTable();
                        }).create();

                AlertDialog alert = builder.create();
                alert.show();
                return false;
            });

            tableLayoutMedicines.addView(tableRow);
        }
    }
}