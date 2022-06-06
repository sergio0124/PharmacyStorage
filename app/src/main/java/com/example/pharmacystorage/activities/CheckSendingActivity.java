package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.MedicineLogic;
import com.example.pharmacystorage.database.logics.SendingLogic;
import com.example.pharmacystorage.models.MedicineModel;
import com.example.pharmacystorage.models.SendingAmount;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

public class CheckSendingActivity extends AppCompatActivity {

    SendingAmount SendingAmount;
    EditText quantityEditText;
    SendingAmount databaseSending;
    MedicineModel medicineData;
    SendingLogic sendingLogic;

    int userId;
    MedicineLogic medicineLogic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sending);

        userId = getIntent().getExtras().getInt("userId");
        Intent intent = getIntent();
        sendingLogic = new SendingLogic(this);
        sendingLogic.open();
        databaseSending = sendingLogic.getSendingAmountsById(SendingAmount.getSendingId()).stream().filter(v -> v.getId() == SendingAmount.getId()).collect(Collectors.toList()).get(0);
        sendingLogic.close();

        medicineLogic = new MedicineLogic(this);
        medicineLogic.open();
        medicineData = medicineLogic.getFilteredListWithQuantityByStorage(userId).stream()
                .filter(v -> v.getId() == databaseSending.getMedicineId())
                .collect(Collectors.toList())
                .get(0);
        medicineLogic.close();

        Bundle arguments = intent.getExtras();

        SendingAmount = (SendingAmount) arguments.getSerializable(SendingAmount.class.getSimpleName());

        quantityEditText = (EditText) findViewById(R.id.edit_text_quality);
        quantityEditText.setText(String.valueOf(SendingAmount.getQuantity()));
        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int count;
                try {
                    count = Integer.parseInt(String.valueOf(quantityEditText.getText()));
                } catch (Exception ex) {
                    Toast.makeText(quantityEditText.getContext(), "Введите число в графу 'Количество'", Toast.LENGTH_LONG).show();
                    return;
                }

                if (count > databaseSending.getQuantity()) {
                    quantityEditText.setText(String.valueOf(databaseSending.getQuantity()));
                    Toast.makeText(quantityEditText.getContext(), "Количество не может быть больше того, что планировалось", Toast.LENGTH_LONG).show();
                    return;
                }

                if (count > medicineData.getQuantityOnStorage()) {
                    quantityEditText.setText(String.valueOf(medicineData.getQuantityOnStorage()));
                    Toast.makeText(quantityEditText.getContext(), "Количество не может быть больше того, что есть на складе", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });


        ((EditText) findViewById(R.id.edit_text_name)).setText(SendingAmount.getName());
        ((EditText) findViewById(R.id.edit_text_name)).setEnabled(false);

        if (SendingAmount.getCost() > medicineData.getQuantityOnStorage()) {
            ((EditText) findViewById(R.id.edit_text_cost)).setText(String.valueOf(medicineData.getQuantityOnStorage()));
        } else {
            ((EditText) findViewById(R.id.edit_text_cost)).setText(String.valueOf(SendingAmount.getCost()));
        }
        ((EditText) findViewById(R.id.edit_text_cost)).setEnabled(false);

        ((Button) findViewById(R.id.button_accept)).setOnClickListener(v -> {
            SendResult("Подтверждено");
        });

        ((Button) findViewById(R.id.button_cancel)).setOnClickListener(v -> {
            SendResult("Ожидание");
        });
    }


    private void SendResult(String status) {
        if (databaseSending.getQuantity() > Integer.parseInt(quantityEditText.getText().toString()) &&
                status.contains("Подтверждено")) {
            status = "Недостача";
        }
        SendingAmount.setStatus(status);
        SendingAmount.setQuantity(Integer.parseInt(quantityEditText.getText().toString()));
        Intent intent = new Intent(this, GetSupplyActivity.class);
        intent.putExtra("SendingAmount", SendingAmount);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}