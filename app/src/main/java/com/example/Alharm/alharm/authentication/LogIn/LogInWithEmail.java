package com.example.Alharm.alharm.authentication.LogIn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Alharm.alharm.Main_Page;
import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.R;
import com.example.Alharm.alharm.authentication.CustomApplication;
import com.example.Alharm.alharm.authentication.CustomSharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.example.Alharm.alharm.authentication.SignUp.SignUp.isValidEmail;

public class LogInWithEmail extends AppCompatActivity {

    private EditText email;
    private EditText password;
    String UserID;

    ArrayList <String> GuidesIDs=new ArrayList<String>();
    ArrayList <String> OfficersIDs=new ArrayList<String>();


    // متغير من نوع  DatabaseReference حتي نستطيع من خلالة عمل access على مكان محدد في قواعد البانات ( firebase )
    DatabaseReference UserRoot;
    // ِِمتغير من نوع  ArrayList تحمل object من User Class والذي يحتوي على بيانات المستخدم
    ArrayList<User> GuidesUsers = new ArrayList<User>();
    ArrayList<User> OfficersUsers = new ArrayList<User>();

    User adminUser = new User();
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_with_email);

        FirebaseApp.initializeApp(this);
        // يتم تحديد المكان الذي نحتاج الوصول الية في ال firebase وهو "Users"
        UserRoot = FirebaseDatabase.getInstance().getReference().getRoot().child("Users");


        progressDialog = new ProgressDialog(this);

        // تعريف ال EditText الخاصة بالبريد الالكتروني وكلمة المرور
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

// انشاء  Instance من ال firebase
        firebaseAuth = FirebaseAuth.getInstance();


        // انشاء  Listener علي ال UserRoot والذي يشير الي مكان بيانات المستخدمين داخل ال firebase حتي يتم استرجاع بيانات المستخدمين للحصول على ال ID الخاص بالمستخدم الحالي بعد تسجيل دخولة
        UserRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : item.getChildren()) {
                        // استرجاع بيانات كل مستخدم ووضعها في object من النوع User Class
                        User user = dataSnapshot1.getValue(User.class);
                       // اذا كان المستخدم مرشد .. نضع بياناتة داخل ال list ألتي تحتوي بيانات المرشدين
                        if (item.getKey().equals("Guide")) {
                            GuidesUsers.add(user);
                            GuidesIDs.add(dataSnapshot1.getKey());
                        }
                            // اذا كان المستخدم ضابط .. نضع بياناتة داخل ال list ألتي تحتوي بيانات الضباط
                        else if (item.getKey().equals("Officer")) {
                            OfficersUsers.add(user);
                            OfficersIDs.add(dataSnapshot1.getKey());
                        }
                            else
                            // اذا كان المستخدم هو ال admin نضعة داخل object منفصل
                            adminUser = user;

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void onLogInBtnClick(View view) {

        // يتم ارجاع البيانات التي قام المسخدم بإدخالها
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        // أذا قام المستخدم بالضغط على زر التسجيل بدون إدخال اى بيانات يتم اظهار رسالة خطأ
        if (emailText.length() == 0) {
            email.setError("ادخل البريد الإلكتروني");
        } else if (!(isValidEmail(emailText))) {
            // اذا قام المستخدم بادخال بريد الكتروني خاطئ يتم اظهار رساة خطأ
            email.setError("عنوان بريد إلكتروني خاطئ");
            email.setFocusable(true);
        } else {
            // اذا لم يدخل المستخدم كلمه المرور يتم اظهار رسالة خطأ
            if (passwordText.length() == 0) {
                password.setError("ادخل كلمة السر");
                // اذا كانت كلمة المرور اقل من 3 احرف او ارقام يتم اظهار رسالة خطأ
            } else if (passwordText.length() < 6 || passwordText.length() > 32) {
                password.setError("كلمة السر يجب الا تقل عن 6 خانات");
            } else {
//اذا لم يتم ادخال شئ خاطئ يتم استدعاء Function MainLogIn
                logIn(emailText, passwordText);
            }
        }


    }


    // عند استدعاء MainLogIn function
    private void logIn(final String email, final String pass) {

// انشاء  progressDialog ليخبر المستخدم بالإنتظار حتي يتم تسجيل بياناته
        progressDialog.setMessage("انتظر من فضلك ..");
        progressDialog.show();

        // تسجيل الدخول للمستخدم في ال  firebaseAuth عن طريق  signInWithEmailAndPassword function
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // اذا تم التسجيل بنحاج
                        if (task.isSuccessful()) {
                            // يتم غلق ال  progressDialog
                            progressDialog.dismiss();
                            openNextPage();
                        } else {
                            // اذا كانت البيانات خاطئة
                            Toast.makeText(LogInWithEmail.this, "البيانات التى ادخلتها غير صحيحة", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }



    // عند الضغط على زر التالي
    private void openNextPage() {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
       // نعرف intent للإنتقال الى الصفحة التالية
        Intent intent = new Intent(LogInWithEmail.this, Main_Page.class);
        Gson gson = ((CustomApplication) getApplication()).getGsonObject();
        CustomSharedPreference pref = ((CustomApplication) getApplication()).getShared();
        String userDataString;


        // نقوم بعمل loop على ال list التي تحتوي على بيانات كل المرشدين للبحث عن المستخدم الحالى داخلهم
        for (int x = 0; x < GuidesUsers.size(); x++) {
           // اذا وجدنا بيانات المستخدم الحالى داخل ال list التي تحتوي على بيانات كل المرشدين نضع هذة البيانات داخل ال  SharedPreference بالإضافة الي وضع نوع المستخدم ك مرشد
            if (emailText.equals(GuidesUsers.get(x).getEmail()) && passwordText.equals(GuidesUsers.get(x).getPassword())) {
                userDataString = gson.toJson(GuidesUsers.get(x));

                UserID=GuidesIDs.get(x);
                pref.setUserData(userDataString);
              // نضع حالة تسجيل المستخدم ب true لتأكيد ان المستخدم قد قام بتسجيل الدخول
                pref.setUserLogInState("true");
                pref.setUserType("Guide");

                pref.setUserID(UserID);
                // الإنتقال الى الصفحة الرئيسية
                startActivity(intent);
                LogInWithEmail.this.finish();

            }

        }

        // نقوم بعمل loop على ال list التي تحتوي على بيانات كل المرشدين للبحث عن المستخدم الحالى داخلهم
        for (int x = 0; x < OfficersUsers.size(); x++) {
            // اذا وجدنا بيانات المستخدم الحالى داخل ال list التي تحتوي على بيانات كل الضباط نضع هذة البيانات داخل ال  SharedPreference بالإضافة الي وضع نوع المستخدم ك ضابط
            if (emailText.equals(OfficersUsers.get(x).getEmail()) && passwordText.equals(OfficersUsers.get(x).getPassword())) {
                userDataString = gson.toJson(OfficersUsers.get(x));
                UserID=OfficersIDs.get(x);

                pref.setUserData(userDataString);
                pref.setUserLogInState("true");
                pref.setUserType("Officer");

                pref.setUserID(UserID);
                startActivity(intent);
                LogInWithEmail.this.finish();

            }

        }
    }


}
