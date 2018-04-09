package com.example.Alharm.alharm.authentication.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.Alharm.alharm.R;
import com.example.Alharm.alharm.authentication.SignUp.SignUp;

public class MainLogIn extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


    }


    // عند الضغط على تسجيل الدخول بالبريد الالكتروني
    public void logInWithEmail(View view) {
     startActivity(new Intent(MainLogIn.this,LogInWithEmail.class));
        MainLogIn.this.finish();

    }

    // عند الضغط على تسجيل الدخول بالبصمة
    public void logInWithFingerPrint(View view) {
        startActivity(new Intent(MainLogIn.this,LogInWithFingerPrint.class));
        MainLogIn.this.finish();

    }



    // عند الضغط على تسجيل حساب جديد
    public void signUp(View view) {
        Intent intent;
        intent=new Intent(MainLogIn.this,SignUp.class);
        startActivity(intent);
        MainLogIn.this.finish();

    }
}