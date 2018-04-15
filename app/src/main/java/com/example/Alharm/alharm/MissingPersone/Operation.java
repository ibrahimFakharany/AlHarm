package com.example.Alharm.alharm.MissingPersone;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;

/**
 * Created by 450 G1 on 18/07/2017.
 */

public class Operation {
    Uri contentUri;
    String mCurrentPhotoPath;

    private void galleryAddPic(ReportMissingPersone profileActivity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        profileActivity.sendBroadcast(mediaScanIntent);
    }

    boolean hasPermission(Activity activity, boolean flag){
        int requestCode;

        if (!flag) {
            // open gallary
            requestCode = 2;
        }else
            requestCode = 1;


        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "permission not guranteed", Toast.LENGTH_SHORT).show();

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "need this permission to store the images", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                return false;
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                return false;
            }
        } else {

            return true;



        }
    }

    // method for pick picture from gallary
    void dispatchPickPictureIntent(Activity activity, int requestCode){
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Ensure that there's a gallary activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null)
            activity.startActivityForResult(takePictureIntent, requestCode);
        else
            Toast.makeText(activity, "There is no app to handle this action", Toast.LENGTH_SHORT).show();

    }



}
