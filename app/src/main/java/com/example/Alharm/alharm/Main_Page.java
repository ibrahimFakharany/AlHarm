package com.example.Alharm.alharm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Alharm.alharm.GuidePerson.ManageGroups;
import com.example.Alharm.alharm.MissingPersone.MissingPersonPage;
import com.example.Alharm.alharm.MissingPersone.ReportMissingPersone;
import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.NormalUser.Groups.groupTracking;
import com.example.Alharm.alharm.NormalUser.MainPlaces.MainPlaces;
import com.example.Alharm.alharm.authentication.CustomApplication;
import com.example.Alharm.alharm.authentication.CustomSharedPreference;
import com.example.Alharm.alharm.authentication.LogIn.MainLogIn;
import com.google.gson.Gson;

public class Main_Page extends AppCompatActivity {

    private String user_type = null;

    String isLoggedIn = "false";
    Button log_out_btn;
    TextView name, phone;
    protected static Gson mGson;
    protected static CustomSharedPreference mPref;
    private static User mUser;
    private static String userString;

    DrawerLayout drawerLayout;
    Button reportMissingPerson, manageGroups, logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        drawerLayout = (DrawerLayout) findViewById(R.id.design_drawer_layout);

        log_out_btn = (Button) findViewById(R.id.log_out_btn);

        name = (TextView) findViewById(R.id.name_drawer);
        phone = (TextView) findViewById(R.id.phone_drawer);


        name.setText("سائح");
        phone.setText("");

        log_out_btn.setVisibility(View.GONE);

        reportMissingPerson = (Button) findViewById(R.id.report_missing_person_bt);
        manageGroups = (Button) findViewById(R.id.manageGroups);

        mGson = ((CustomApplication) getApplication()).getGsonObject();
        mPref = ((CustomApplication) getApplication()).getShared();

        // الحصول على بيانات المستخدم  من SharedPreference
        userString = mPref.getUserData();
        mUser = mGson.fromJson(userString, User.class);

        // الحصول على نوع المستخدم من  SharedPreference
        user_type = mPref.getUserType();

        // الحصول على حالة تسجيل المستخدم من  SharedPreference
        isLoggedIn = mPref.getUserLogInState();
        // اذا كان المستخدم قام بتسجيل الدخول او قام بتسجيل حساب جديد
        if (isLoggedIn.equals("true")) {
            // يتم اظهار الاسم ورقم الهاتف وزر تسجيل الخروج
            name.setVisibility(View.VISIBLE);
            phone.setVisibility(View.VISIBLE);
            log_out_btn.setVisibility(View.VISIBLE);

            // وضع اسم المستخدم ورقم هاتفة
            name.setText(mUser.getName());
            phone.setText(mUser.getPhone());
            // اذا كان المستخدم مرشد يتم اظهار زر ادارة الحملات
            if (user_type.equals("Guide")) {
                reportMissingPerson.setVisibility(View.GONE);
            }
            // اذا كان المستخدم ضابط يتم اظهار زر التبليغ عن شخص مفقود
            else {
                manageGroups.setVisibility(View.GONE);
            }
        }

    }

    public void reportMissingPersone(View view) {
        Intent intent;
        if (isLoggedIn.equals("true")) {
            intent = new Intent(Main_Page.this, ReportMissingPersone.class);
        } else {
            Toast.makeText(this, "يجب تسجيل الدخول اولا !", Toast.LENGTH_SHORT).show();
            intent = new Intent(Main_Page.this, MainLogIn.class);
            //intent =new Intent(Main_Page.this,UploadDocument.class);

        }
        startActivity(intent);

    }

    public void MainPlaces(View view) {
        Intent intent = new Intent(Main_Page.this, MainPlaces.class);
        startActivity(intent);
    }

    public void missingPersonPage(View view) {
        Intent intent = new Intent(Main_Page.this, MissingPersonPage.class);
        startActivity(intent);
    }

    // الضغط على زر ادارة المجموعات
    public void ManageGroups(View view) {
        Intent intent;
        if (isLoggedIn.equals("true")) {
            intent = new Intent(Main_Page.this, ManageGroups.class);
        } else {
            Toast.makeText(this, "يجب تسجيل الدخول اولا !", Toast.LENGTH_SHORT).show();
            intent = new Intent(Main_Page.this, MainLogIn.class);
        }
        startActivity(intent);
    }

    // الضغط على زر تتبع الحملات
    public void groupTracking(View view) {
        // الانتقال الى صفحة تتبع الحملات
        Intent intent = new Intent(Main_Page.this, groupTracking.class);
        startActivity(intent);
    }

    // عند الضغط على زر تسجيل الخروج
    public void logOut(View view) {

        // وضع قيمة حالة تسجيل المستخدم ب  false والانتقال الي الصفحة الرئيسية من جديد
        mPref.setUserLogInState("false");
        Intent intent = new Intent(Main_Page.this, Main_Page.class);
        startActivity(intent);
        Main_Page.this.finish();
    }

    public void contactUs(View view) {

        Toast.makeText(this, "غير متوفرة !", Toast.LENGTH_SHORT).show();
    }

    public void openDrawer(View view) {
        drawerLayout.openDrawer(Gravity.START);
    }
}
