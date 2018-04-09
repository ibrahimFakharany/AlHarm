package com.example.Alharm.alharm.NormalUser.MainPlaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.Alharm.alharm.R;

public class MainPlaces extends AppCompatActivity {
    // صفحة عرض الأماكن الرئيسية

Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_places);
       // تعريف  intent للإنتقال الى صفحة الأماكن التي يختارها للمستخدم
        intent=new Intent(MainPlaces.this,MainPlacesList.class);

    }


// عند الضغط على زر El Madina ( الأماكن الرئيسية بالمدينة )
    public void madinaMainPlaces(View view) {
        // يتم ارسال اسم المدينة الي الصفحة التالة ليتم عرض الاماكن الرئيسية بالمدينة
        intent.putExtra("place","Madina");
        startActivity(intent);
    }

    // عند الضغط على زر Makkah  ( الأماكن الرئيسية بمكة )
    public void makkahMainPlaces(View view) {
        // يتم ارسال اسم مكة الي الصفحة التالة ليتم عرض الاماكن الرئيسية بمكة
        intent.putExtra("place","Makkah");
        startActivity(intent);
    }



}
