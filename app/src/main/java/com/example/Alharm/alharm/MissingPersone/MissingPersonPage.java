package com.example.Alharm.alharm.MissingPersone;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.Alharm.alharm.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class MissingPersonPage extends AppCompatActivity implements SearchImageDialogListener, SearchNameDialogListener {
    static final String FRAGMENT_SEARCH_FLAG_KEY = "search_by";
    static final String FLAG_NAME = "name";
    static final String FLAG_IMAGE = "image";
    private static final int REQUEST_PERMISSION_SCREEN = 1001;
    private static final int SELECT_PICTURE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    static String NAME_KEY = "name";
    static String IMAGE_PATH_KEY = "imagePath";
    Uri contentUri;
    Uri imageUri;
    String mCurrentPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_person_page);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_missing_people);
        MissingPersonPageFragment peopleFragment = new MissingPersonPageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.missing_page_fragment, peopleFragment, "").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.missing_people_menu, menu);
        return true;
    }

    private void galleryAddPic(MissingPersonPage missingPersonPage) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        missingPersonPage.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SCREEN) {

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                MissingPersonPageFragment peopleFragment = new MissingPersonPageFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.missing_page_fragment, peopleFragment, "").commit();

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
            Uri selectedImageUri = data.getData();
            CropImage.activity(selectedImageUri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                SearchResultFragment fragment = new SearchResultFragment();

                Bundle bundle = new Bundle();
                bundle.putString(FRAGMENT_SEARCH_FLAG_KEY, FLAG_IMAGE);
                bundle.putString(IMAGE_PATH_KEY, imageUri.toString());
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.missing_page_fragment, fragment, "")
                        .commit();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "error" + CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.search:

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
                            MissingPersonPageFragment peopleFragment = new MissingPersonPageFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.missing_page_fragment, peopleFragment, "").commit();

                        }
                    });
                    alert1.show();
                }else{

                    DialogSearch dialogSearch = new DialogSearch();
                    dialogSearch.show(getSupportFragmentManager(), "");
                }
                break;

        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onFinishNameDialog(String name) {
        SearchResultFragment fragment = new SearchResultFragment();

        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_SEARCH_FLAG_KEY, FLAG_NAME);
        bundle.putString(NAME_KEY, name);

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.missing_page_fragment, fragment, "").commit();
    }


    @Override
    public void onFinshImageDialog(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
        File f = new File(mCurrentPhotoPath);
        contentUri = Uri.fromFile(f);


    }
}


