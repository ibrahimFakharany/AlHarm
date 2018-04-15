package com.example.Alharm.alharm.authentication.SignUp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class UploadDocument extends AppCompatActivity {
    private static final String TAG = "UploadDocument";
    Button upload, submit;
    private static final int SELECT_PICTURE = 2;
    private static final int REQUEST_PERMISSION_SCREEN = 1002;

    private static final String EMAIL_KEY = "email";
    private static final String NAME_KEY = "name";

    String email , name, password, userType, phone = null;

    User user = null;
    ImageView documentImg;
    ImageButton remove;
    Uri selectedImageUri;

    private StorageReference mStorageRef;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);

        Bundle bundle = getIntent().getExtras();
        user  = (User) bundle.getSerializable("user Data");
        mStorageRef = FirebaseStorage.getInstance().getReference();



        if(user !=null ){
            name = user.getName();
            email = user.getEmail();
            password = user.getPassword();
            userType = bundle.getString("user type");
            phone = user.getPhone();


            upload = findViewById(R.id.upload);
            submit = findViewById(R.id.submit);
            documentImg = findViewById(R.id.document_img);
            remove = findViewById(R.id.remove);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    documentImg.setImageDrawable(getResources().getDrawable(R.drawable.placeholder128));
                    selectedImageUri = null;
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitData();
                }
            });

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchPickPictureIntent();

                }
            });

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder alert1 = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
                alert1.setTitle("");
                alert1.setMessage("من فضلك فعل الصلاحيات");
                alert1.setCancelable(false);
                alert1.setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivityForResult(intent, REQUEST_PERMISSION_SCREEN);
                    }
                });

                alert1.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
                alert1.show();
            }
        }else {


        }

    }

    private void submitData() {
        if(selectedImageUri != null){

            progressBar = new ProgressDialog(this, R.style.Theme_MyDialog);
            progressBar.setMessage(getString(R.string.uploading_text));
            progressBar.setCancelable(false);
            progressBar.show();
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()));

            StorageReference sR_filePath = mStorageRef.child("requests").child(name + timeStamp);
            final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
            sR_filePath.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    selectedImageUri = taskSnapshot.getDownloadUrl();
                    mReference.child("requests").push()
                            .setValue(new User(name,
                                    phone,
                                    email,
                                    password,
                                    selectedImageUri.toString(),
                                    userType,
                                    "pending",
                                    "0"));
                    progressBar.dismiss();
                    Toast.makeText(getBaseContext(), "طلبك قيد التنفيذ", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    progressBar.dismiss();
                    Toast.makeText(getBaseContext(), "خطا فى الرفع", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            // choosing image from gallery
            if (resultCode == RESULT_OK) {
                selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    documentImg.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    selectedImageUri = null;
                    Toast.makeText(this, "خطأ", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void dispatchPickPictureIntent() {
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Ensure that there's a gallary activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(takePictureIntent, SELECT_PICTURE);
        else
            Toast.makeText(this, "There is no app to handle this action", Toast.LENGTH_SHORT).show();


    }

}
