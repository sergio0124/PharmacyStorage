package com.example.pharmacystorage.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.PharmacyLogic;
import com.example.pharmacystorage.helper_models.Validators;
import com.example.pharmacystorage.models.PharmacyModel;

public class CreateClientActivity extends AppCompatActivity {

    TableRow selectedRow;
    Button button_create_pharmacy;
    Button button_cancel;
    PharmacyLogic logic;
    int userId;
    int id;
    EditText edit_text_name;
    EditText edit_text_email;
    EditText edit_text_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_client);
        userId = getIntent().getExtras().getInt("userId");
        id = getIntent().getExtras().getInt("id");

        button_create_pharmacy = findViewById(R.id.button_save);
        button_cancel = findViewById(R.id.button_cancel);

        logic = new PharmacyLogic(this);
        edit_text_name = findViewById(R.id.edit_text_name);
        edit_text_email = findViewById(R.id.edit_text_email);
        edit_text_address = findViewById(R.id.edit_text_address);

        if(id != 0){
            edit_text_name.setText(getIntent().getExtras().getString("name"));
            edit_text_email.setText(getIntent().getExtras().getString("email"));
            edit_text_address.setText(getIntent().getExtras().getString("address"));
        }

        button_create_pharmacy.setOnClickListener(
                v -> { ;

                    PharmacyModel model = new PharmacyModel(edit_text_name.getText().toString(), edit_text_email.getText().toString(),
                            edit_text_address.getText().toString(), userId);

                    if (!Validators.validateEmail(model.getEmail())){
                        errorDialog("???????????????? ???????????? ??????????");
                        return;
                    }

                    logic.open();

                    for (PharmacyModel mod : logic.getFullList()){
                        if (mod.getEmail().equals(model.getEmail()) && model.getId() != mod.getId()){
                            errorDialog("?????????? ?????????? ?????? ????????");
                            return;
                        }
                    }

                    if(id != 0){
                        model.setId(id);
                        logic.update(model);
                    } else {
                        logic.insert(model);
                    }
                    logic.close();
                    this.finish();
                }
        );

        button_cancel.setOnClickListener(
                v -> {
                    finish();
                }
        );

    }

    private void errorDialog(String err){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateClientActivity.this);
        builder.setMessage(err);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "????",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }
}