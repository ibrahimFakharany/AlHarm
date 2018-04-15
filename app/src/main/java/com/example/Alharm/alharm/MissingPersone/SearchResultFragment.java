package com.example.Alharm.alharm.MissingPersone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Alharm.alharm.MissingPersone.Data.DatabaseContract;
import com.example.Alharm.alharm.R;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.example.Alharm.alharm.MissingPersone.MissingPersonPage.FLAG_IMAGE;
import static com.example.Alharm.alharm.MissingPersone.MissingPersonPage.FLAG_NAME;

/**
 * Created by 450 G1 on 25/03/2018.
 */

public class SearchResultFragment extends Fragment {
    private static final String TAG = "SearchResultFragment";
    RecyclerView recyclerView;
    Bundle bundle;

    private static Bitmap bmp, yourSelectedImage, bmpimg1, bmpimg2;
    private static ImageView iv1, iv2;
    private static TextView tv;
    private static String path1, path2;
    private static String text;
    private static Button start;
    private static int imgNo = 0;
    private static Uri selectedImage;
    private static InputStream imageStream;
    private static long startTime, endTime;
    private static final int SELECT_PHOTO = 100;
    private static String descriptorType;
    private static int min_dist = 10;
    private static int min_matches = 750;
    private static Bitmap bitmap1, bitmap2;
    static Context context;
    String imagePath;
    KairosListener listener = null;
    Kairos myKairos = null;
    static final String GALLARY_ID = "missingpeople";
    ArrayList<MissingPersonModel> personsResultList = null;
    ProgressDialog progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        listener = new KairosListener() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, s);


                JSONObject root;
                try {
                    root = new JSONObject(s);
                    double confidence = 0.0;
                    String subject_id = "";
                    JSONArray images = root.getJSONArray("images");
                    JSONObject image = null;
                    personsResultList = new ArrayList<>();
                    for (int i = 0; i < images.length(); i++) {

                        image = images.getJSONObject(i);
                        JSONArray candidates = image.getJSONArray("candidates");
                        JSONObject candidate = null;

                        for (int j = 0; j < candidates.length(); j++) {
                            candidate = candidates.getJSONObject(j);
                            confidence = candidate.getDouble("confidence");
                            subject_id = candidate.getString("subject_id");
                            personsResultList.add(new MissingPersonModel(subject_id, confidence));
                        }


                    }
                    Cursor cursor = getActivity().getContentResolver().query(DatabaseContract.CONTENT_URI, new String[]{DatabaseContract.TableColumns.COLUMN_NAME, DatabaseContract.TableColumns.COLUMN_IMAGE, DatabaseContract.TableColumns.COLUMN_FIREBASE_ID}, null, null, null);

                    List<MissingPersonModel> datafromDB = loadListOfImages(cursor);
                    ArrayList<MissingPersonModel> lastResult = new ArrayList<>();
                    for (MissingPersonModel personModel : personsResultList) {

                        subject_id = personModel.getFirebaseKey();
                        confidence = personModel.getConfidence();
                        for (MissingPersonModel personModelfromDB : datafromDB) {

                            if (personModelfromDB.getFirebaseKey().equals(subject_id)) {
                                MissingPersonModel myPer = new MissingPersonModel(
                                        subject_id,
                                        personModelfromDB.getName(),
                                        personModelfromDB.getImgUrl(),
                                        confidence,
                                        personModelfromDB.getLat(),
                                        personModelfromDB.getLang());

                                if (personModelfromDB.getPhone() != null && personModelfromDB.getPhone().length() > 0)
                                    myPer.setPhone(personModelfromDB.getPhone());

                                lastResult.add(myPer);

                            }

                        }

                    }
                    Log.d(TAG, "last result size >> " + Double.toString(lastResult.size()));
                    progressBar.dismiss();

                    MissingPeopleAdapter adapter = new MissingPeopleAdapter(getActivity(), lastResult);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    progressBar.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.images_not_found), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String s) {
                progressBar.dismiss();

            }
        };


        // set authentication
        String app_id = "acdba0d7";
        String api_key = "c9398669bf70e9d7327f7f951134ffa7";
        myKairos = new Kairos();
        myKairos.setAuthentication(getActivity(), app_id, api_key);

        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("life", "onResume");


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_search_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bundle = getArguments();

        String searchBy = bundle.getString(MissingPersonPage.FRAGMENT_SEARCH_FLAG_KEY);
        switch (searchBy) {

            case FLAG_NAME:

                String name = bundle.getString(MissingPersonPage.NAME_KEY);
                handleSearchName(name);

                break;
            case FLAG_IMAGE:

                imagePath = bundle.getString(MissingPersonPage.IMAGE_PATH_KEY);
                handleSearchImage(imagePath);
                break;

        }


    }

    private void handleSearchName(String name) {
        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
        String pName = "", pImage = "", firebaseId = "";
        Double lat = 0.0, lang = 0.0;

        ArrayList<MissingPersonModel> people = new ArrayList<>();
        String[] projection = new String[]{

                DatabaseContract.TableColumns.COLUMN_NAME,
                DatabaseContract.TableColumns.COLUMN_FIREBASE_ID,
                DatabaseContract.TableColumns.COLUMN_IMAGE,
                DatabaseContract.TableColumns.COLUMN_LAT,
                DatabaseContract.TableColumns.COLUMN_LONG,

        };
        Cursor cursor = getActivity().getContentResolver().query(DatabaseContract.CONTENT_URI, projection, DatabaseContract.TableColumns.COLUMN_NAME + " = ? ", new String[]{name}, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    firebaseId = cursor.getString(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_FIREBASE_ID));
                    pName = cursor.getString(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_NAME));
                    pImage = cursor.getString(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_IMAGE));
                    lat = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_LAT));
                    lang = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_LONG));

                    people.add(new MissingPersonModel(firebaseId, pName, pImage, lat, lang));

                } while (cursor.moveToNext());

            }
        }

        if (people.size() > 0) {

            // populate listview
            MissingPeopleAdapter adapter = new MissingPeopleAdapter(getActivity(), people);
            recyclerView.setAdapter(adapter);
        }
    }

    private void handleSearchImage(String imagePath) {
        List<MissingPersonModel> list = new ArrayList<>();
        Log.e("SearchResultFragment", "handle image search");
        // the main image is the image selected by the user (camera or gallary)
        Uri mainImage = Uri.parse(imagePath);

        try {
            bitmap1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mainImage);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (bitmap1 == null) {


            Toast.makeText(getContext(), getString(R.string.image_nissed), Toast.LENGTH_SHORT).show();

        } else {
            progressBar = new ProgressDialog(getActivity(), R.style.Theme_MyDialog);
            progressBar.setMessage(getString(R.string.loading_text));
            progressBar.setCancelable(false);

            try {
                String selector = "FULL";
                String threshold = "0.50";
                if (!((Activity) getActivity()).isFinishing()) {

                    progressBar.show();
                }
                myKairos.recognize(bitmap1, GALLARY_ID, selector, threshold, null, null, listener);

            } catch (JSONException e) {
                e.printStackTrace();
                progressBar.dismiss();
                Toast.makeText(getContext(), "error4", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                progressBar.dismiss();

                Toast.makeText(getContext(), "error5", Toast.LENGTH_SHORT).show();

            }

        }


    }

    private List<MissingPersonModel> loadListOfImages(Cursor cursor) {
        List<MissingPersonModel> result = new ArrayList<>();

        if (cursor != null) {
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                String name, image, firebaseId, phone;
                double lat, lang = 0.0;

                do {

                    name = cursor.getString(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_NAME));
                    image = cursor.getString(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_IMAGE));
                    firebaseId = cursor.getString(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_FIREBASE_ID));
                    lat = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_LAT));
                    lang = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_LONG));
                    phone = cursor.getString(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_PHONE));
                    if (phone != null && phone.length() > 0)
                        result.add(new MissingPersonModel(firebaseId, name, image, lat, lang, phone));
                    else
                        result.add(new MissingPersonModel(firebaseId, name, image, lat, lang));

                } while (cursor.moveToNext());


            }

        }
        return result;

    }


}