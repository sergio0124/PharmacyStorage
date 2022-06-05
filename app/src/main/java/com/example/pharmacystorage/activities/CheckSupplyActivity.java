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
import com.example.pharmacystorage.models.SupplyAmount;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CheckSupplyActivity extends AppCompatActivity {

    SupplyAmount supplyAmount;
    EditText quantityEditText;
    Button buttonDate;
    Calendar date = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_supply);

        supplyAmount = getIntent().getExtras().getParcelable("supplyAmount");
        buttonDate = findViewById(R.id.buttonDate);
        date = GregorianCalendar.getInstance();
        String text = date==null? "Choose date" : date.get(Calendar.DAY_OF_MONTH) + " / " +
                date.get(Calendar.MONTH) + " / " + (date.get(Calendar.YEAR));
        buttonDate.setText(text);

        quantityEditText = (EditText) findViewById(R.id.edit_text_count);
        quantityEditText.setText(supplyAmount.getQuantity());
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
                    quantityEditText.setText("0");
                    Toast.makeText(quantityEditText.getContext(), "Количество не может быть больше того, что планировалось", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        buttonDate.setOnClickListener(
                v -> {
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            date.set(year, monthOfYear + 1, dayOfMonth);
                            String text = date.getTime().getDate() + " / " +
                                    date.getTime().getMonth() + " / " + (date.getTime().getYear()+ 1900);
                            buttonDate.setText(text);
                            supplyAmount.setEndDate(date);
                        }
                    };
                    DatePickerDialog datePickerDialog;
                    datePickerDialog = new DatePickerDialog(buttonDate.getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                            dateSetListener, 2022, 6, 1);

                    datePickerDialog.show();
                }
        );

        ((EditText) findViewById(R.id.edit_text_name)).setText(supplyAmount.getName());
        ((EditText) findViewById(R.id.edit_text_name)).setEnabled(false);

        ((EditText) findViewById(R.id.edit_text_cost)).setText(supplyAmount.getCost());
        ((EditText) findViewById(R.id.edit_text_cost)).setEnabled(false);

        ((Button) findViewById(R.id.button_accept)).setOnClickListener(v->{
            SendResult("Подтверждено");
        });

        ((Button) findViewById(R.id.button_cancel)).setOnClickListener(v->{
            SendResult("Ожидание");
        });

        ((Button) findViewById(R.id.button_defective)).setOnClickListener(v->{
            SendResult("Брак");
        });
    }



    private void SendResult(String status){
        if(supplyAmount.getQuantity() > Integer.parseInt(quantityEditText.getText().toString()) &&
                status.contains("Подтверждено")){
            status = "Недостача";
        }
        supplyAmount.setState(status);

        Intent intent = new Intent(this, GetSupplyActivity.class);
        intent.putExtra("supplyAmount", supplyAmount);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}