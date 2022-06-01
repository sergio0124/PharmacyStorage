package com.example.pharmacystorage.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.MainActivity;
import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.models.StorageModel;

import java.util.Arrays;
import java.util.List;

public class EnterActivity extends AppCompatActivity {

    Button button_to_register_activity;
    Button button_enter;
    EditText editTextLogin;
    EditText editTextPassword;

    StorageLogic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        button_to_register_activity = findViewById(R.id.button_to_register_activity);
        button_enter = findViewById(R.id.button_enter);
        editTextLogin = findViewById(R.id.edit_text_login);
        editTextPassword = findViewById(R.id.edit_text_password);

        logic = new StorageLogic(this);

        button_to_register_activity.setOnClickListener(
                v -> {
                    Intent intent = new Intent(EnterActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
        );

        button_enter.setOnClickListener(
                v -> {
                    StorageModel model = new StorageModel(editTextLogin.getText().toString(), editTextPassword.getText().toString());

                    logic.open();

                    List<StorageModel> storages = logic.getFullList();
                    for(StorageModel storage : storages){
                        if(storage.getName().equals(model.getName()) && storage.getPassword().equals(model.getPassword())){
                            logic.close();

                            this.finish();
                            Intent intent = new Intent(EnterActivity.this, MainMenuActivity.class);
                            intent.putExtra("userId", storage.getId());
                            startActivity(intent);

                            return;
                        }
                    }

                    logic.close();

                    AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
                    builder.setMessage("Пароль введен неверно или такой логин не зарегистрирован");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "ОК",
                            (dialog, id) -> dialog.cancel());

                    AlertDialog alert = builder.create();
                    alert.show();
                }
        );
    }
}