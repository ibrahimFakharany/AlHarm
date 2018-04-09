package com.example.Alharm.alharm.authentication.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.R;

import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
RadioGroup user_type;
    RadioButton Guide,Officer;

    EditText userName, userEmail, userPhone, userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button next;
        next=(Button)findViewById(R.id.next);

        next.setVisibility(View.VISIBLE);

        // تعريف ال EditTexts , RadioButton الخاصة بإدخال بيانات المستخدم
        userName = (EditText) findViewById(R.id.name);
        userEmail = (EditText) findViewById(R.id.email);
        userPhone = (EditText) findViewById(R.id.phone);
        userPass = (EditText) findViewById(R.id.password);

        user_type=(RadioGroup)findViewById(R.id.user_type);
        Guide=(RadioButton)findViewById(R.id.SignUpAsGuide);
        Officer=(RadioButton)findViewById(R.id.SignUpAsOfficer);



    }

    public void next(View view){

// التحقق من ان المستخدم قام بادخال كل البيانات المطلوبه واظهار رسالة خطأ اذا كان المسنخدم لم يدخل احد البيانات
        boolean flag = true;
        if (userName.length() == 0) {
            userName.setError("مطلوب !");
            userName.setFocusable(true);
            flag = false;
        } else if (!(Pattern.matches("^[\\p{L} .'-]+$", userName.getText()))) {
            userName.setError("يسمح فقط بالأحرف والمسافات");
            userName.setFocusable(true);
            flag = false;
        }
        if (userEmail.length() == 0) {
            userEmail.setError("مطلوب !");
            userEmail.setFocusable(true);
            flag = false;
        } else if (!(isValidEmail(userEmail.getText().toString()))) {
            userEmail.setError("عنوان بريد إلكتروني خاطئ");
            userEmail.setFocusable(true);
            flag = false;
        }
        if (userPhone.length() == 0) {
            userPhone.setError("مطلوب !");
            userPhone.setFocusable(true);
            flag = false;
        }
        if (userPass.length() == 0) {
            userPass.setError("مطلوب !");
            userPass.setFocusable(true);
            flag = false;
        } else if (userPass.length() < 6) {
            userPass.setError("على الاقل 6 خانات");
            userPass.setFocusable(true);
            flag = false;
        }
            if(!Guide.isChecked()&&!Officer.isChecked()){
                flag=false;
                Toast.makeText(this, "يجب اختيار نوع المستخدم", Toast.LENGTH_LONG).show();
            }

        // اذا كانت كل البيانات صحيحة وقد تم ادخال كل البيانات المطلوبه
        if (flag){
            RadioButton tmp=(RadioButton)findViewById(user_type.getCheckedRadioButtonId());
           // نضع البيانات التي قام المستخدم بادخالها داخل object من نوع User
            User user=new User();
            user.setName(userName.getText().toString());
            user.setPhone(userPhone.getText().toString());
            user.setEmail(userEmail.getText().toString());
            user.setPassword(userPass.getText().toString());

         // الانقال الي صفحة تأكيد البصمة
            Intent intent =new Intent(SignUp.this,ConfirmFingerprint.class);
            Bundle bundle = new Bundle();
         // ارسال بيانات المستخدم الى الصفحة التالية
            bundle.putSerializable("user Data",user);
           // ارسال نوع المستخدم
            String user_type=tmp.getText().toString();
            if (user_type.equals("مرشد"))
            bundle.putString("user type","Guide");
            else
                bundle.putString("user type","Officer");

            intent.putExtras(bundle);
            startActivity(intent);
            SignUp.this.finish();
        }



    }
    // التحقق من شكل البريد الالكتروني
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
