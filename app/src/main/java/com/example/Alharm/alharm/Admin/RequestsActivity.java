package com.example.Alharm.alharm.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestsActivity extends AppCompatActivity {
    DatabaseReference ref = null;
    ProgressDialog progressBar;
    ArrayList<User> list =null;
    User user = null;
    TextView messageText;
    RecyclerView requestsRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        progressBar = new ProgressDialog(this, R.style.Theme_MyDialog);
        progressBar.setMessage(getString(R.string.loading_text));
        progressBar.setCancelable(false);
        ref = FirebaseDatabase.getInstance().getReference("requests");
        requestsRv = findViewById(R.id.request_rv);
        requestsRv.setLayoutManager(new LinearLayoutManager(this));

        messageText = findViewById(R.id.message_text);

        list = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFinishing()) {

            progressBar.show();

        }
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot personSnapshot : dataSnapshot.getChildren()) {
                    HashMap map = (HashMap) personSnapshot.getValue();
                    user = new User(personSnapshot.getKey(),
                            (String) map.get("name"),
                            (String) map.get("phone"),
                            (String) map.get("email"),
                            (String) map.get("password"),
                            (String) map.get("documentUrl"),
                            (String) map.get("userType"),
                            (String) map.get("state"),
                            (String) map.get("code"));

                    list.add(user);
                }
                if (list.size() == 0) {
                    messageText.setVisibility(View.VISIBLE);
                    requestsRv.setVisibility(View.GONE);


                }else {
                    messageText.setVisibility(View.GONE);
                    requestsRv.setVisibility(View.VISIBLE);
                    RequestsAdapter adapter = new RequestsAdapter(list, RequestsActivity.this);

                    adapter.setListener(new RequestsAdapter.RequestOnClickListener() {
                        @Override
                        public void onRequestClick(User user) {
                            Intent intent = new Intent(RequestsActivity.this, RequestDetail.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", user);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    requestsRv.setAdapter(adapter);

                }
                progressBar.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.dismiss();

            }
        });
    }


}