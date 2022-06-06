package com.example.pharmacystorage.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
    StorageLogic logic;
    BasketLogic logicBasket;

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

        logic = new StorageLogic(this);
        logicBasket = new BasketLogic(this);

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
                    int userId = logic.getFullList().get(logic.getFullList().size()-1).getId();
                    logicBasket.createBasket(userId);

                    logic.insert(model);
                    logicBasket.close();

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
            return Optional.empty();

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
}