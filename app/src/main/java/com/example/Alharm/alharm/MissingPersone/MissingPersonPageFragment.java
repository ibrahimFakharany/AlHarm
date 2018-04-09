package com.example.Alharm.alharm.MissingPersone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.Alharm.alharm.MissingPersone.Data.DatabaseContract;
import com.example.Alharm.alharm.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 450 G1 on 25/03/2018.
 */

public class MissingPersonPageFragment extends Fragment {
    DatabaseReference ref = null;
    MissingPersonModel person = null;
    ArrayList<MissingPersonModel> list;
    RecyclerView rvMissingPeople;
    KairosListener subjectListener;
    KairosListener addListener;
    Kairos mKairos = null;
    ProgressDialog progressBar;
    int counter1 = 0;

    public static String TAG = "MissingPersonPageFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_missing_person_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onviewcreated");
        rvMissingPeople = view.findViewById(R.id.rv_missing_people);
        rvMissingPeople.setLayoutManager(new LinearLayoutManager(getActivity()));
        ref = FirebaseDatabase.getInstance().getReference("People");
        list = new ArrayList<>();

        String app_id = "acdba0d7";
        String api_key = "c9398669bf70e9d7327f7f951134ffa7";
        // each time entered this activity should update the database with new items
        // deleteAllDataIndb();
        progressBar = new ProgressDialog(getActivity(), R.style.Theme_MyDialog);
        progressBar.setMessage(getString(R.string.loading_text));
        progressBar.setCancelable(false);

        mKairos = new Kairos();
        mKairos.setAuthentication(getActivity(), app_id, api_key);

        //get new items from firebase
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!((Activity) getActivity()).isFinishing()) {

                    progressBar.show();

                }
                Log.d(TAG, "onDataChange");
                for (DataSnapshot personSnapshot : dataSnapshot.getChildren()) {
                    HashMap map = (HashMap) personSnapshot.getValue();
                    person = new MissingPersonModel(personSnapshot.getKey(), (String) map.get("name"), (String) map.get("imgUrl"), (Double) map.get("lat"), (Double) map.get("lang"));
                    list.add(person);
                }
                if (list.size() != 0) {
                    updateDB(list);
                    updateKairos();
                    MissingPeopleAdapter adapter = new MissingPeopleAdapter(getActivity(), list);
                    rvMissingPeople.setAdapter(adapter);
                }
                else Toast.makeText(getActivity(), "list is null", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.dismiss();

            }
        });


        // listeners
        subjectListener = new KairosListener() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, s);

                ArrayList<String> subjects = new ArrayList<>();

                try {
                    JSONObject root = new JSONObject(s);
                    JSONArray subjectsJson = root.getJSONArray("subject_ids");

                    for (int i = 0; i < subjectsJson.length(); i++) {
                        subjects.add(subjectsJson.getString(i));
                    }

                    counter1 = 0;

                    MissingPersonModel person = null;
                    int i = 0;
                    for (i = 0; i < list.size(); i++) {
                        person = list.get(i);

                        if (!subjects.contains(person.getFirebaseKey())) {
                            try {
                                counter1++;
                                mKairos.enroll(person.getImgUrl(), person.getFirebaseKey(), SearchResultFragment.GALLARY_ID, null, null, null, addListener);

                            } catch (UnsupportedEncodingException e) {
                                counter1--;
                                e.printStackTrace();

                            }
                        }


                    }


                    if (counter1 == 0)
                        progressBar.dismiss();


                } catch (JSONException e) {

                    counter1 = 0;

                    MissingPersonModel person = null;
                    int i = 0;
                    for (i = 0; i < list.size(); i++) {
                        person = list.get(i);


                        try {
                            counter1++;

                            mKairos.enroll(person.getImgUrl(), person.getFirebaseKey(), SearchResultFragment.GALLARY_ID, null, null, null, addListener);


                        } catch (UnsupportedEncodingException ex) {
                            counter1--;
                            e.printStackTrace();

                        } catch (JSONException ex) {
                            counter1--;
                            e.printStackTrace();
                        }


                    }
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String s) {

                Log.d(TAG, s);

            }
        };

        addListener = new KairosListener() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, s);
                counter1--;
                if (counter1 == 0) {

                    progressBar.dismiss();

                }

            }

            @Override
            public void onFail(String s) {
                Log.d(TAG, s);
            }
        };

    }

    private void updateKairos() {
        String key = "";
        try {

            mKairos.listSubjectsForGallery(SearchResultFragment.GALLARY_ID, subjectListener);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "error1", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "error2", Toast.LENGTH_SHORT).show();

        }

    }

    public ArrayList<String> getListOfPersonsKey() {
        ArrayList<String> list = new ArrayList<>();
        String firebaseId = null;

        String[] projection = new String[]{DatabaseContract.TableColumns.COLUMN_FIREBASE_ID};

        Cursor cursor = getActivity().getContentResolver().query(DatabaseContract.CONTENT_URI, projection, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                do {

                    firebaseId = cursor.getString(cursor.getColumnIndex(DatabaseContract.TableColumns.COLUMN_FIREBASE_ID));
                    list.add(firebaseId);

                } while (cursor.moveToNext());
                return list;


            }
        }
        return null;


    }

    private void updateDB(ArrayList<MissingPersonModel> list) {
        ArrayList<String> keys = getListOfPersonsKey();
        String key = null;
        String state = null;
        ContentValues contentValues = null;

        for (MissingPersonModel person : list) {
            key = person.getFirebaseKey();
            if (keys != null && keys.contains(key)) {

                // update
                state = person.getState();
                contentValues = new ContentValues();
                contentValues.put(DatabaseContract.TableColumns.COLUMN_STATE, state);
                getActivity().getContentResolver()
                        .update(DatabaseContract.CONTENT_URI, contentValues, DatabaseContract.TableColumns.COLUMN_FIREBASE_ID + " = ? ", new String[]{key});

            } else {

                //insert
                contentValues = new ContentValues();
                contentValues.put(DatabaseContract.TableColumns.COLUMN_FIREBASE_ID, person.getFirebaseKey());
                contentValues.put(DatabaseContract.TableColumns.COLUMN_NAME, person.getName());
                contentValues.put(DatabaseContract.TableColumns.COLUMN_IMAGE, person.getImgUrl());
                contentValues.put(DatabaseContract.TableColumns.COLUMN_LAT, person.getLat());
                contentValues.put(DatabaseContract.TableColumns.COLUMN_LONG, person.getLang());
                contentValues.put(DatabaseContract.TableColumns.COLUMN_STATE, person.getState());
                getActivity().getApplicationContext().getContentResolver().insert(DatabaseContract.CONTENT_URI, contentValues);

            }


        }


    }


}
