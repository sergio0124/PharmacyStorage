package com.example.pharmacystorage.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacystorage.R;
import com.example.pharmacystorage.database.logics.StorageLogic;
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

public class EnterActivity extends AppCompatActivity {

    Button button_to_register_activity;
    Button button_enter;
    EditText editTextLogin;
    EditText editTextPassword;

    StorageLogic logic;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                        if(storage.getName().equals(model.getName()) && verifyPassword(model.getPassword(), storage.getPassword(), salt)){
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
}