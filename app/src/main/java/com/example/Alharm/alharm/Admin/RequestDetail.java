package com.example.Alharm.alharm.Admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Alharm.alharm.Mail.mail;
import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Random;

public class RequestDetail extends AppCompatActivity implements View.OnClickListener {
    TextView name, email, phone, userType;
    ImageView documentImg;
    Button showDoc, accept, reject;
    User user = null;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request);

        name = findViewById(R.id.name_request);
        email = findViewById(R.id.email_request);
        phone = findViewById(R.id.phone_request);
        userType = findViewById(R.id.user_type_request);
        documentImg = findViewById(R.id.document_img);
        showDoc = findViewById(R.id.show_document_btn);
        accept = findViewById(R.id.accept_btn);
        reject = findViewById(R.id.reject_btn);

        user = (User) getIntent().getExtras().getSerializable("user");
        name.setText(user.getName());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        userType.setText((user.getUserType().equals("Officer"))?"موظف": "مرشد");

        progressDialog = new ProgressDialog(this);

        showDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(documentImg.getDrawable() == null){

                    showDoc.setText("اخفاء");
                    progressDialog.setMessage("انتظر من فضلك ..");
                    progressDialog.show();
                    Picasso.with(RequestDetail.this).load(user.getDocumentUrl()).into(documentImg,new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(RequestDetail.this, "خطأ", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    });

                }else {

                    showDoc.setText("اظهار");
                    documentImg.setImageDrawable(null);

                }

            }
        });


        accept.setOnClickListener(this);
        reject.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.accept_btn:
                String number = GenerateNumber();

                acceptUser(number);
                break;
            case R.id.reject_btn:
                rejectUser();

                break;
        }
    }

    private void rejectUser() {
        deleteUserFromRequests();
        deleteRequestDocument();

    }

    private void deleteRequestDocument() {
        String documentUrl = user.getDocumentUrl();

        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(documentUrl);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                sendingRejectMail(user.getName(),user.getEmail());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(RequestDetail.this, "خطأ اعد المحاوله", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendingRejectMail(String name, String email) {
        mail mail = new mail();
        // ونحضر الماسدج
        String msg = "Hello "+ name +",\n" +
                "\n" +
                "Fortunatily we are sorry to inform you that your request is rejected.\n" +
                "Almurshid Team";
        // وبعدين نعمل ارسال
        mail.sendMail(email, "Confirmation Code from Almurshid", msg);
        finish();
    }

    public String GenerateNumber() {
        // نعمل رقم عضوائى من 4 ارقام
        String ret = "";
        for (int i = 0; i < 4; i++) {
            Random rn = new Random();
            // نعمل راندم
            int n = rn.nextInt(9) + 1;
            ret += n + "";
        }
        return ret;
    }

    // to reject the user



    // to accept the user
    // three steps
    private void acceptUser(String code) {
        deleteUserFromRequests();
        moveUserToItsLocationInFirebase(code);
        sendingMail(user.getName(), user.getEmail(), code);
    }

    //step1
    private void deleteUserFromRequests() {
        String firebaseKey = user.getFirebaseKey();
        FirebaseDatabase.getInstance().getReference().child("requests").child(firebaseKey).removeValue();

    }

    //step2
    private void moveUserToItsLocationInFirebase(String code) {
        String userType = user.getUserType();
        storeUserData(userType,code);
    }

    //step3
    public void storeUserData(String user_type, String code){
        user.setCode(code);
        user.setState("sending");
        HashMap<String, Object> userMap = user.toMap();
        DatabaseReference Root = FirebaseDatabase.getInstance().getReference().getRoot();
        // انشاء key  جديد للمستخدم
        Root.child("Users").child(user_type).push().setValue(user);
    }

    private void sendingMail(String name, String email,String number) {
        mail mail = new mail();
        // ونحضر الماسدج
        String msg = "Hello "+ name +",\n" +
                "\n" +
                "Thank you for registering with Almurshid.\n" +
                "you have been accpeted from admin\n" +
                "To confirm your email, please write the number when logging.\n" +
                "The number is " + number + " .\n" +
                "Almurshid Team";
        // وبعدين نعمل ارسال
        mail.sendMail(email, "Confirmation Code from Almurshid", msg);
        finish();
    }


}
