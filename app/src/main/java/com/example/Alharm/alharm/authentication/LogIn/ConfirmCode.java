package com.example.Alharm.alharm.authentication.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.R;
import com.example.Alharm.alharm.authentication.SignUp.ConfirmFingerprint;

public class ConfirmCode extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    EditText codeET;
    Button submitBtn;
    String code = null;

    String firebaseKey = null;
    Bundle bundle = null;
    public static String PAGE_TYPE_KEY = "page";
    public static String PAGE_TYPE_CONFIRMATION_CODE = "confirmationCode";
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_code);
        Intent intent = getIntent();
        codeET = findViewById(R.id.et_code);
        codeET.addTextChangedListener(this);
        submitBtn = findViewById(R.id.submit);
        toast = Toast.makeText(this, "خطأ فى الكود", Toast.LENGTH_SHORT);
        if (intent != null) {
            bundle = intent.getExtras();
            if (bundle != null) {
                User user = (User) bundle.getSerializable("user Data");
                firebaseKey = bundle.getString("firebaseKey");
                code = user.getCode();
                if (firebaseKey != null || code != null) {
                    submitBtn.setOnClickListener(this);

                } else {
                    Toast.makeText(this, "code or firebase key is null", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    @Override
    public void onClick(View view) {
        String enteredCode = codeET.getText().toString();
        if (enteredCode.length() > 0 && enteredCode != null) {
            if (code != null) {
                if (code.equals(enteredCode)) {
                    // code is correct
                    Intent intent = new Intent(this, ConfirmFingerprint.class);
                    if (bundle != null) {
                        bundle.putString(PAGE_TYPE_KEY, PAGE_TYPE_CONFIRMATION_CODE);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                } else
                    Toast.makeText(this, "خطأ فى الكود", Toast.LENGTH_SHORT).show();

            } else
                Toast.makeText(this, "code is null", Toast.LENGTH_SHORT).show();

        } else
            Toast.makeText(this, "من فضلك ادخل الكود", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == 4) {
            String enteredCode = codeET.getText().toString();
            if (enteredCode.length() > 0 && enteredCode != null) {
                if (code != null) {
                    if (code.equals(enteredCode)) {
                        // code is correct
                        final Intent intent = new Intent(this, ConfirmFingerprint.class);
                        if (bundle != null) {
                            bundle.putString(PAGE_TYPE_KEY, PAGE_TYPE_CONFIRMATION_CODE);
                            intent.putExtras(bundle);
                            Thread t = new Thread() {
                                public void run() {
                                    try {
                                        sleep(30);
                                        startActivity(intent);
                                        finish();
                                    } catch (Exception e) {
                                    }

                                }
                            };
                            t.start();


                        }
                    } else {

                        Thread t = new Thread() {
                            public void run() {
                                try {
                                    sleep(10);
                                    codeET.setText("");
                                } catch (Exception e) {
                                }
                            }
                        };
                        t.start();

                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                }

            }


        }

    }
}
