package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.PharmacyLogic;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.PharmacyLogic;
import com.example.pharmacystorage.database.logics.RequestLogic;
import com.example.pharmacystorage.database.logics.SendingLogic;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.models.PharmacyModel;
import com.example.pharmacystorage.models.MedicineModel;
import com.example.pharmacystorage.models.PharmacyModel;
import com.example.pharmacystorage.models.RequestAmount;
import com.example.pharmacystorage.models.RequestModel;
import com.example.pharmacystorage.models.SendingAmount;
import com.example.pharmacystorage.models.SendingModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CreateSendActivity extends AppCompatActivity {

    TableRow selectedRow;
    Spinner spinner_pharmacy;
    Spinner spinner_medicine;
    MedicineLogic logicM;
    StorageLogic logicS;
    RequestLogic logicR;
    PharmacyLogic logicP;
    SendingLogic logicSending;
    Date date;
    ArrayList<SendingAmount> sendingAmounts = new ArrayList<>();
    List<String> titles = Arrays.asList("Наименование", "Кол-во", "Цена шт.");
    int userId;
    int iid = 0;
    Button button_add;
    Button button_save;
    Button button_cancel;
    EditText edit_count;
    EditText edit_cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_send);

        date = new Date();
        logicM = new MedicineLogic(this);
        logicS = new StorageLogic(this);
        logicR = new RequestLogic(this);
        logicP = new PharmacyLogic(this);
        logicSending = new SendingLogic(this);
        spinner_pharmacy = findViewById(R.id.spinner_pharmacy_name);
        spinner_medicine = findViewById(R.id.spinner_medicine_name);

        userId = getIntent().getExtras().getInt("userId");
        iid = getIntent().getExtras().getInt("id");

        button_add = findViewById(R.id.button_add);
        button_save = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);
        edit_cost = findViewById(R.id.edit_text_cost);
        edit_count = findViewById(R.id.edit_text_count);

        button_add.setOnClickListener(v -> {
            MedicineModel item =
                    (MedicineModel) spinner_medicine.getItemAtPosition(spinner_medicine.getSelectedItemPosition());
            String Name = item.toString();
            int count = Integer.parseInt(edit_count.getText().toString());
            int cost = Integer.parseInt(edit_cost.getText().toString());

            for (SendingAmount model: sendingAmounts) {
                if (model.getMedicineId() == item.getId() && item.getId() != 0){
                    model.setCost(cost);
                    model.setQuantity(count);
                    fillTable();
                    return;
                }
            }

            SendingAmount amount = new SendingAmount();
            amount.setMedicineId(item.getId());
            amount.setCost(cost);
            amount.setName(Name);
            amount.setQuantity(count);
            sendingAmounts.add(amount);
            fillTable();
        });

        button_save.setOnClickListener(v -> {
            if(sendingAmounts.size()>0){
                try {
                    SaveSending();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                finish();
            }
            else {
                Toast.makeText(this, "Добавьте лекарства", Toast.LENGTH_LONG).show();
            }
        });

        button_cancel.setOnClickListener(v -> finish());

        LoadData();
    }


    private void SaveSending(){
        //Save Request for first
        //Save Request_Medicines
        logicSending.open();

        PharmacyModel item = (PharmacyModel) spinner_pharmacy.getItemAtPosition(spinner_pharmacy.getSelectedItemPosition());
        SendingModel sendingModel = new SendingModel();
        sendingModel.setPharmacyId(item.getId());
        sendingModel.setDate(Calendar.getInstance());
        sendingModel.setStorageId(userId);
        logicSending.insert(sendingModel);

        int sendingId = logicSending.getFullList().get(logicSending.getFullList().size()-1).getId();
        sendingAmounts.forEach(v->v.setSendingId(sendingId));

        logicSending.insertSendingAmounts(sendingAmounts);
        logicSending.close();
    }



    private void LoadData() {

        logicP.open();
        List<PharmacyModel> spinnerArray = logicP.getFilteredList(userId);
        logicP.close();

        ArrayAdapter<PharmacyModel> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_pharmacy.setAdapter(adapter);



        logicM.open();
        List<MedicineModel> spinnerArrayMeds = logicM.getFilteredList(userId);
        ArrayAdapter<MedicineModel> adapterMeds = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArrayMeds);
        adapterMeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_medicine.setAdapter(adapterMeds);
        logicM.close();

        if(iid >0){
            button_add.setVisibility(View.INVISIBLE);
            button_save.setVisibility(View.INVISIBLE);
            logicSending.open();
            logicP.open();

            PharmacyModel pharmacyModel = logicP.getElement(iid);
            ArrayAdapter<String> adapterNoChoose = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, Collections.singletonList(pharmacyModel.getName()));
            spinner_pharmacy.setAdapter(adapterNoChoose);
            spinner_pharmacy.setEnabled(false);
            spinner_medicine.setAdapter(null);

            List<SendingAmount> amounts = logicSending.getSendingAmountsById(iid);
            logicR.close();
            logicP.close();
            sendingAmounts = (ArrayList<SendingAmount>) amounts;
            fillTable();
        }
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


        for (SendingAmount amount : sendingAmounts) {
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

                selectedRow = tableRow;

                for(int i = 0; i < tableLayoutMedicines.getChildCount(); i++){
                    View view = tableLayoutMedicines.getChildAt(i);
                    if (view instanceof TableRow){
                        view.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                    }
                }
                tableRow.setBackgroundColor(Color.parseColor("#FFBB86FC"));

                String nameField = ((TextView) selectedRow.getChildAt(0)).getText().toString();
                SendingAmount model = new SendingAmount();
                model.setName(nameField);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Удалить запись?");
                builder.setNegativeButton("Отмена", (dialog, id) -> dialog.cancel());

                builder.setPositiveButton("Да",
                        (dialog, which) -> {
                            sendingAmounts.removeIf(rec ->rec.getName().equals(model.getName()));
                            fillTable();
                            dialog.dismiss();
                        }).create();

                AlertDialog alert = builder.create();
                alert.show();
                return false;
            });

            tableLayoutMedicines.addView(tableRow);
        }
    }
}