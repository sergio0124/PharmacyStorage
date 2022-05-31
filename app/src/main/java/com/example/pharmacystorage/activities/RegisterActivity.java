package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.models.StorageModel;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    Button buttonRegister;
    EditText editTextLogin;
    EditText editTextPassword;
    EditText editTextEmail;
    EditText editTextEmailPassword;
    StorageLogic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = findViewById(R.id.button_register);
        editTextLogin = findViewById(R.id.edit_text_login);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextEmailPassword = findViewById(R.id.edit_text_email_password);

        logic = new StorageLogic(this);

        buttonRegister.setOnClickListener(
                v -> {
                    StorageModel model = new StorageModel(editTextLogin.getText().toString(), editTextPassword.getText().toString(),
                            editTextEmail.getText().toString(), editTextEmailPassword.getText().toString());

                    logic.open();

                    List<StorageModel> storages = logic.getFullList();

                    for (StorageModel storage : storages) {
                        if (storage.getName().equals(model.getName())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setMessage("Такой логин уже зарегистрирован");
                            builder.setCancelable(true);

                            builder.setPositiveButton(
                                    "ОК",
                                    (dialog, id) -> dialog.cancel());

                            AlertDialog alert = builder.create();
                            alert.show();
                            return;
                        }
                    }

                    logic.insert(model);
                    logic.close();

                    this.finish();
                    Intent intent = new Intent(RegisterActivity.this, EnterActivity.class);
                    startActivity(intent);
                }
        );
    }
}