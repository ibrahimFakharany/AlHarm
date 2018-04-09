package com.example.Alharm.alharm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


public class Splashage extends AppCompatActivity {

    private static int  SPLASH_Time_Out = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent destination;
                destination = new Intent(Splashage.this,Main_Page.class);
                startActivity(destination);
                finish();
            }
        } ,SPLASH_Time_Out);
    }
}
