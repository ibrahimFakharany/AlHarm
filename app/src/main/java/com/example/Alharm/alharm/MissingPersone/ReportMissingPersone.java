package com.example.Alharm.alharm.MissingPersone;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Alharm.alharm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ReportMissingPersone extends AppCompatActivity implements View.OnClickListener, DialogListener {
    private static final String TAG = "ReportMissingPersone";

    Button reportButton, uploadImageButton, defingLocationButton;
    EditText personName;
    CustomBottomSheetDialog customBottomSheetDialog;
    String mCurrentPhotoPath;
    Uri contentUri;
    LocationManager mLocationManager = null;
    private static final int REQUEST_PERMISSION_SCREEN = 1002;
    private static final int SELECT_PICTURE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_PERMISSION = 1;
    private static final int CROP_REQUEST_CODE = 1;
    private static final int EDIT_USER_NAME_ACTIVITY = 5;
    private StorageReference mStorageRef;
    ProgressDialog progressBar;

    Uri imageP;
    Uri imageUri;
    android.location.Location myLocation = null;
    String state = "missed";
    TextView personLocation;
    ImageView personImage;
    ImageButton removeImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_missing_persone);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.report_missing_person));
        personName = (EditText) findViewById(R.id.name);
        reportButton = (Button) findViewById(R.id.report);
        reportButton.setOnClickListener(this);
        uploadImageButton = (Button) findViewById(R.id.upload_image);
        uploadImageButton.setOnClickListener(this);
        defingLocationButton = (Button) findViewById(R.id.define_location);
        defingLocationButton.setOnClickListener(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        customBottomSheetDialog = new CustomBottomSheetDialog();
        customBottomSheetDialog.setListener(this);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        personImage = findViewById(R.id.person_image);
        personLocation = findViewById(R.id.person_location);
        removeImage = findViewById(R.id.remove);
        removeImage.setOnClickListener(this);
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


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            },
                            1);
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                        1, mLocationListener);
            }
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            myLocation = location;
            personLocation.setText(Double.toString(myLocation.getLatitude()) + " , " + Double.toString(myLocation.getLongitude()));
            progressBar.dismiss();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            progressBar.dismiss();
            Toast.makeText(getBaseContext(), getString(R.string.get_location_failed), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            progressBar.dismiss();
            Toast.makeText(ReportMissingPersone.this, getString(R.string.request_enabled_location), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.upload_image:
                customBottomSheetDialog.show(getSupportFragmentManager(), ReportMissingPersone.class.getSimpleName());
                break;
            case R.id.report:
                uploadData();
                break;
            case R.id.define_location:
                getMyLocation();
                break;

            case R.id.remove:
                removeImageAction();
                break;
        }
    }

    private void removeImageAction() {
        personImage.setImageDrawable(getResources().getDrawable(R.drawable.placeholder128));
        imageUri = null;
    }

    private void uploadData() {
        if (imageUri != null) {

            if (personName.getText().toString().length() > 0) {

                if (myLocation != null) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    progressBar = new ProgressDialog(this, R.style.Theme_MyDialog);
                    progressBar.setMessage(getString(R.string.uploading_text));
                    progressBar.setCancelable(false);
                    progressBar.show();
                    final String personNameStr = personName.getText().toString();
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()));

                    StorageReference sR_filePath = mStorageRef.child(personNameStr + timeStamp);
                    final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
                    sR_filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageP = taskSnapshot.getDownloadUrl();
                            mReference.child("People").push()
                                    .setValue(new MissingPersonModel(personNameStr, imageP.toString(), myLocation.getLatitude(), myLocation.getLongitude(), state));
                            progressBar.dismiss();
                            Toast.makeText(getBaseContext(), "تم الرفع", Toast.LENGTH_SHORT).show();
                            resetFields();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            progressBar.dismiss();
                            Toast.makeText(getBaseContext(), "خطا فى الرفع", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, getString(R.string.insert_location_warning), Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, getString(R.string.insert_name_warning), Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, getString(R.string.upload_image_warning), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void resetFields() {

        personImage.setImageDrawable(getDrawable(R.drawable.placeholder128));
        imageUri =  null;
        personName.setText("");

    }

    @Override
    public void sendData(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
        File f = new File(mCurrentPhotoPath);
        contentUri = Uri.fromFile(f);
    }

    private void galleryAddPic(ReportMissingPersone profileActivity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PERMISSION_SCREEN) {

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "on activity result from action permission");
                finish();
            }

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                galleryAddPic(this);
                CropImage.activity(contentUri).setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);

            } else {
                // have to delete the created file
                File fdelete = new File(contentUri.getPath());
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Log.e(ReportMissingPersone.class.getSimpleName(), "deleted");
                    } else {
                        Log.e(ReportMissingPersone.class.getSimpleName(), "didn't delete");

                    }
                }

            }
        } else if (requestCode == SELECT_PICTURE) {
            // choosing image from gallery
            if (data != null) {
                Uri selectedImageUri = data.getData();
                CropImage.activity(selectedImageUri).setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    personImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "error" + CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE, Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    1);
            return;
        }
        progressBar = new ProgressDialog(this, R.style.Theme_MyDialog);
        progressBar.setMessage(getString(R.string.please_wait));
        progressBar.setCancelable(false);
        progressBar.show();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                2, mLocationListener);
    }
}
