package com.example.pharmacystorage.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.BasketLogic;
import com.example.pharmacystorage.database.logics.StorageLogic;
import com.example.pharmacystorage.helper_models.Validators;
import com.example.pharmacystorage.models.StorageModel;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class RegisterActivity extends AppCompatActivity {

    Button buttonRegister;
    EditText editTextLogin;
    EditText editTextPassword;
    EditText editTextEmail;
    EditText editTextEmailPassword;
    TextView textViewPassword;
    StorageLogic logic;
    BasketLogic logicBasket;
    int userId;
    boolean check;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = findViewById(R.id.button_register);
        editTextLogin = findViewById(R.id.edit_text_login);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextEmailPassword = findViewById(R.id.edit_text_email_password);
        textViewPassword = findViewById(R.id.text_view_password);
        userId = getIntent().getExtras().getInt("userId");
        check = true;

        logic = new StorageLogic(this);
        logicBasket = new BasketLogic(this);

        if (userId != 0){
            textViewPassword.setText("Текущий пароль(если меняете пароль)");
            buttonRegister.setText("Изменить");
            logic.open();
            StorageModel model = logic.getElement(userId);
            editTextLogin.setText(model.getName());
            editTextPassword.setText("");
            editTextEmail.setText(model.getEmail());
            editTextEmailPassword.setText(model.getEmailPassword());
            logic.close();
        }

        buttonRegister.setOnClickListener(
                v -> {
                    check = true;
                    logic.open();
                    StorageModel currentModel = logic.getElement(userId);
                    StorageModel model = new StorageModel(editTextLogin.getText().toString(), editTextPassword.getText().toString(),
                            editTextEmail.getText().toString(), editTextEmailPassword.getText().toString());

                    if (userId != 0){
                        if (!editTextPassword.getText().toString().equals("")){
                            if(verifyPassword(editTextPassword.getText().toString(), currentModel.getPassword(), salt)){

                                edit_password_alert(currentModel, model);


                            }else {
                                logic.close();
                                return;
                            }
                        } else{
                            currentModel.setName(editTextLogin.getText().toString());
                            currentModel.setEmail(editTextEmail.getText().toString());
                            currentModel.setEmailPassword(editTextEmailPassword.getText().toString());



                            // Валидация почты
                            if (!Validators.validateEmail(model.getEmail())){
                                errorDialog("Неверный формат почты");
                                return;
                            }
                            logic.update(currentModel);
                            logic.close();
                            Intent intent = new Intent(RegisterActivity.this, EnterActivity.class);
                            startActivity(intent);
                            return;
                        }


                    }else {
                        logic.close();

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



                        model.setPassword(hashPassword(model.getPassword(), salt).get().toString());

                        logic.open();
                        logicBasket.open();

                        List<StorageModel> storages = logic.getFullList();

                        for (StorageModel storage : storages) {
                            if (storage.getName().equals(model.getName()) || storage.getEmail().equals(model.getEmail())) {
                                errorDialog("Пользователь с такими данными уже зарегестрирован");
                                return;
                            }
                        }
                        logic.insert(model);
                        int userId = logic.getFullList().get(logic.getFullList().size()-1).getId();
                        logicBasket.createBasket(userId);


                        logicBasket.close();

                        this.finish();
                        Intent intent = new Intent(RegisterActivity.this, EnterActivity.class);
                        startActivity(intent);
                    }

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

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 512;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    String salt = "gghisghsighf'lkgh;lkbnhojkfbnhoisdhfoi[dhf[oidshf'lskanghsld;fkghsf;lgkhfd'kglhfdlskghoiah[praio;fhg[oiafh;oiha;khg;lfahg;lglfdhglkahgoafh'";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Optional hashPassword (String password, String salt) {

        char[] chars = password.toCharArray();
        byte[] bytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

        Arrays.fill(chars, Character.MIN_VALUE);

        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] securePassword = fac.generateSecret(spec).getEncoded();
            return Optional.of(Base64.getEncoder().encodeToString(securePassword));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("Exception encountered in hashPassword()");
            return Optional.of(password);

        } finally {
            spec.clearPassword();
        }
    }

    private static final SecureRandom RAND = new SecureRandom();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Optional generateSalt (final int length) {

        if (length < 1) {
            System.err.println("error in generateSalt: length must be > 0");
            return Optional.empty();
        }

        byte[] salt = new byte[length];
        RAND.nextBytes(salt);

        return Optional.of(Base64.getEncoder().encodeToString(salt));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean verifyPassword (String password, String key, String salt) {
        Optional optEncrypted = hashPassword(password, salt);
        if (!optEncrypted.isPresent()) return false;
        return optEncrypted.get().equals(key);
    }

    private void edit_password_alert(StorageModel currentModel, StorageModel model){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Изменение пароля");
        alert.setMessage("Введите новый пароль");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int whichButton) {
                editTextPassword.setText(input.getText());

                currentModel.setPassword(editTextPassword.getText().toString());
                if(currentModel.getPassword().toString().equals("")){
                    return;
                }

                // Валидация пароля
                if (!Validators.validatePassword(model.getPassword())){
                    errorDialog("Пароль должен содержать цифры, строчный латинский символ, заглавный латинский символ, " +
                            "cодержать по крайней мере один специальный символ, такой как ! @ # & ( ), пароль должен содержать не менее 4 символов и не более 20 символов.");
                    return;
                }

                currentModel.setPassword(hashPassword(currentModel.getPassword(), salt).get().toString());

                currentModel.setName(editTextLogin.getText().toString());
                currentModel.setEmail(editTextEmail.getText().toString());
                currentModel.setEmailPassword(editTextEmailPassword.getText().toString());



                // Валидация почты
                if (!Validators.validateEmail(model.getEmail())){
                    errorDialog("Неверный формат почты");
                    return;
                }
                logic.update(currentModel);
                logic.close();
                Intent intent = new Intent(RegisterActivity.this, EnterActivity.class);
                startActivity(intent);
                return;
                // Do something with value!
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                editTextPassword.setText("");
                check = false;
                // Canceled.
            }
        });

        alert.show();
    }
}