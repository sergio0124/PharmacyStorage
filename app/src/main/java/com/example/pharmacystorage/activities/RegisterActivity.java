package com.example.pharmacystorage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.helper_models.Validators;
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

                    // Валидация пароля
                    if (!Validators.validatePassword(model.getPassword())){
                        errorDialog("Пароль должен содержать цифры, строчный латинский символ, заглавный латинский символ, " +
                                "cодержать по крайней мере один специальный символ, такой как ! @ # & ( ), пароль должен содержать не менее 4 символов и не более 20 символов.");
                        return;
                    }

                    // Валидация почты
                    if (!Validators.validateEmail(model.getEmail())){
                        errorDialog("Неверный формат почты");
                        return;
                    }

                    logic.open();

                    List<StorageModel> storages = logic.getFullList();

                    for (StorageModel storage : storages) {
                        if (storage.getName().equals(model.getName()) || storage.getEmail().equals(model.getEmail())) {
                            errorDialog("Пользователь с такими данными уже зарегестрирован");
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

    private void errorDialog(String err){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setMessage(err);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "ОК",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }
}