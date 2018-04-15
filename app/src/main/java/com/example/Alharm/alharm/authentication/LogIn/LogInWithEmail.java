package com.example.Alharm.alharm.authentication.LogIn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Alharm.alharm.Admin.RequestsActivity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.example.Alharm.alharm.authentication.SignUp.SignUp.isValidEmail;

public class LogInWithEmail extends AppCompatActivity {

    private static final String TAG = "LogInWithEmail";
    private EditText email;
    private EditText password;
    String UserID;

    ArrayList<String> GuidesIDs = new ArrayList<String>();
    ArrayList<String> OfficersIDs = new ArrayList<String>();


    // متغير من نوع  DatabaseReference حتي نستطيع من خلالة عمل access على مكان محدد في قواعد البانات ( firebase )
    DatabaseReference UserRoot;
    // ِِمتغير من نوع  ArrayList تحمل object من User Class والذي يحتوي على بيانات المستخدم
    ArrayList<User> GuidesUsers = new ArrayList<User>();
    ArrayList<User> OfficersUsers = new ArrayList<User>();

    User adminUser = new User();
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    boolean check = false;

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

        progressDialog.setMessage("انتظر من فضلك ..");
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                        } else if (item.getKey().equals("Admin")) {
                            adminUser = user;

                        }
                        // اذا كان المستخدم هو ال admin نضعة داخل object منفصل
                        Log.e(TAG, "admin data:: " + adminUser.getEmail() + " " + adminUser.getPassword());

                        Log.e(TAG, "size of lists guide" + GuidesUsers.size() + " officer " + OfficersUsers.size());
                        progressDialog.dismiss();

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
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

                // تسجيل الدخول للمستخدم في ال  firebaseAuth عن طريق  signInWithEmailAndPassword function
                logIn(emailText, passwordText);


            }
        }


    }


    // عند استدعاء MainLogIn function
    private void logIn(final String email, final String pass) {
       /* HashMap<String, User> map = getUser(email, pass);
        if (map != null) {
            Iterator<String> keys = map.keySet().iterator();

            if (keys.hasNext()) {
                Log.e("hema", "entered has next");
                String key = keys.next();
                User user = map.get(key);
                if (user.getState().equals("authorized") || key.equals("admin")) {
                    Log.e("hema", "entered authorized");
*/
        progressDialog.setMessage("انتظر من فضلك ..");
        progressDialog.show();

        // تسجيل الدخول للمستخدم في ال  firebaseAuth عن طريق  signInWithEmailAndPassword function
        Log.e(TAG, email+" "+pass);

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // اذا تم التسجيل بنحاج
                        if (task.isSuccessful()) {
                            // يتم غلق ال  progressDialog
                            progressDialog.dismiss();
                            FirebaseUser user = task.getResult().getUser();
                            String userId = user.getUid();
                            if (userId.equals("lSp3mg5NcdTHxH69ibTxxQlXAwQ2")) {
                                //admin login
                                openAdminPage();

                            } else {

                                openNextPage();

                            }
                        } else {

                            User user = searchInDatabase(email, pass);

                            if(user != null){
                                String state = user.getUser().getState();
                                Log.e(TAG, state);

                                Bundle bundle = new Bundle();
                                bundle.putString("", user.getFirebaseKey());
                                bundle.putSerializable("user Data", user.getUser());
                                Intent intent = new Intent(LogInWithEmail.this, ConfirmCode.class);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }else {
                                // اذا كانت البيانات خاطئة
                                Toast.makeText(LogInWithEmail.this, "البيانات التى ادخلتها غير صحيحة", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();

                        }
                    }
                });

             /*   } else {
                    Log.e("hema", "entered not authorized");

                    // user not authorized
                    // open code page
                    Bundle bundle = new Bundle();
                    bundle.putString("firebaseKey", key);
                    bundle.putSerializable("user", user);
                    Intent intent = new Intent(this, ConfirmCode.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            }


        } else {
            // credentials not found
            Toast.makeText(this, "خطأ فى البيانات", Toast.LENGTH_SHORT).show();
        }*/

    }

    private User searchInDatabase(String email, String pass) {
        Log.e(TAG, "admin " + email + " " + pass);
        for (int i = 0; i < GuidesUsers.size(); i++) {
            if (email.equals(GuidesUsers.get(i).getEmail())) {
               if(pass.equals(GuidesUsers.get(i).getPassword())){

                   return new User(GuidesIDs.get(i), GuidesUsers.get(i));
               }
            }

        }
        for (int i = 0; i < OfficersUsers.size(); i++) {
            if (email.equals(OfficersUsers.get(i).getEmail())) {
                if(pass.equals(OfficersUsers.get(i).getPassword())){

                    return new User(OfficersIDs.get(i), OfficersUsers.get(i));

                }
            }

        }
        return null;
    }

    /*private HashMap<String, User> getUser(String userEmail, String userPass) {
        Log.e(TAG, "admin " + adminUser.getEmail() + " " + userEmail);
        Log.e(TAG, "admin " + adminUser.getPassword() + " " + userPass);

        HashMap<String, User> hashMap = new HashMap<>();
        userEmail = "ahmed";

//        String adminEmail= adminUser.getEmail();
        String adminEmail = "admin";

        String pass = adminUser.getPassword();
        if (userEmail.equals(adminEmail)) {
            if (userPass.equals(pass)) {
                hashMap.put("admin", new User());
                return hashMap;
            } else Log.e(TAG, "pass not equal");

        } else Log.e(TAG, "email not equal");
        User user = null;
        for (int i = 0; i < GuidesUsers.size(); i++) {
            user = GuidesUsers.get(i);
            if (userEmail.equals(user.getEmail()) && userPass.equals(user.getPassword())) {
                hashMap.put(GuidesIDs.get(i), user);
                return hashMap;
            }
        }

        for (int i = 0; i < OfficersUsers.size(); i++) {

            user = OfficersUsers.get(i);
            if (userEmail.equals(user.getEmail()) && userPass.equals(user.getPassword())) {
                hashMap.put(OfficersIDs.get(i), user);
                return hashMap;

            }
        }


        return null;
    }*/

    private void openAdminPage() {
        Intent intent = new Intent(this, RequestsActivity.class);
        startActivity(intent);
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

                UserID = GuidesIDs.get(x);
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
                UserID = OfficersIDs.get(x);

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
