package com.example.pharmacystorage.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.models.SupplyAmount;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CheckSupplyActivity extends AppCompatActivity {

    SupplyAmount supplyAmount;
    EditText quantityEditText;
    Button buttonDate;
    Calendar date = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_supply);
        Intent intent = getIntent();

        Bundle arguments = intent.getExtras();

        supplyAmount = (SupplyAmount) arguments.getSerializable(SupplyAmount.class.getSimpleName());
        buttonDate = findViewById(R.id.buttonDate);

        date = GregorianCalendar.getInstance();
        date.setTimeZone(TimeZone.getDefault());
        String text = date == null ? "Choose date" : date.getTime().getDate() + "/" + date.getTime().getMonth() + "/" + (date.getTime().getYear() + 1900);
        buttonDate.setText(text);


        quantityEditText = (EditText) findViewById(R.id.edit_text_quality);
        quantityEditText.setText(String.valueOf(supplyAmount.getQuantity()));
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

                if (count > supplyAmount.getQuantity()) {
                    quantityEditText.setText(String.valueOf(supplyAmount.getQuantity()));
                    Toast.makeText(quantityEditText.getContext(), "Количество не может быть больше того, что планировалось", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        buttonDate.setOnClickListener(
                v -> {
                    DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
                        Calendar testDate = new GregorianCalendar();
                        testDate.set(year, monthOfYear, dayOfMonth);
                        if (testDate.getTime().getTime() < GregorianCalendar.getInstance().getTime().getTime()) {
                            Toast.makeText(this, "Дата не может быть раньше сегодня", Toast.LENGTH_LONG).show();
                            return;
                        }

                        date.set(year, monthOfYear, dayOfMonth);
                        String text1 = date.get(Calendar.DAY_OF_MONTH) + "/" +
                                date.get(Calendar.MONTH) + "/" + (date.get(Calendar.YEAR));
                        buttonDate.setText(text1);
                        supplyAmount.setEndDate(date);
                    };
                    DatePickerDialog datePickerDialog;
                    Calendar testDate = GregorianCalendar.getInstance();
                    datePickerDialog = new DatePickerDialog(buttonDate.getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                            dateSetListener, testDate.get(Calendar.YEAR),
                            testDate.get(Calendar.MONTH),
                            testDate.get(Calendar.DAY_OF_MONTH));

                    datePickerDialog.show();
                }
        );

        ((EditText) findViewById(R.id.edit_text_name)).setText(supplyAmount.getName());
        ((EditText) findViewById(R.id.edit_text_name)).setEnabled(false);

        ((EditText) findViewById(R.id.edit_text_cost)).setText(String.valueOf(supplyAmount.getCost()));
        ((EditText) findViewById(R.id.edit_text_cost)).setEnabled(false);

        ((Button) findViewById(R.id.button_accept)).setOnClickListener(v -> {
            SendResult("Подтверждено");
        });

        ((Button) findViewById(R.id.button_cancel)).setOnClickListener(v -> {
            SendResult("Ожидание");
        });

        ((Button) findViewById(R.id.button_defective)).setOnClickListener(v -> {
            SendResult("Брак");
        });
    }


    private void SendResult(String status) {
        if (supplyAmount.getQuantity() > Integer.parseInt(quantityEditText.getText().toString()) &&
                status.contains("Подтверждено")) {
            status = "Недостача";
        }
        supplyAmount.setState(status);
        supplyAmount.setQuantity(Integer.parseInt(quantityEditText.getText().toString()));
        supplyAmount.setEndDate(date);
        Intent intent = new Intent(this, GetSupplyActivity.class);
        intent.putExtra("SupplyAmount", supplyAmount);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}