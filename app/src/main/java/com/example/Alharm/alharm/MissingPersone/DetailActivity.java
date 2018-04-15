package com.example.Alharm.alharm.MissingPersone;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Alharm.alharm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    ImageView imageView;
    TextView namePerson, phonePerson;
    Button getRoute, foundBtn;
    String name, imgUrl;
    double lat, longtude;
    String firebaseId;
    String phoneTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageView = findViewById(R.id.img_detail_person);
        namePerson = findViewById(R.id.name_detail_person);
        getRoute = findViewById(R.id.get_route);
        foundBtn = findViewById(R.id.found_btn);
        phonePerson = findViewById(R.id.phone);

        foundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyDialog();
            }
        });
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0.0);
        longtude = intent.getDoubleExtra("long", 0.0);
        name = intent.getStringExtra("name");
        imgUrl = intent.getStringExtra("imgUrl");
        firebaseId = intent.getStringExtra("firebaseId");
        phoneTxt = intent.getStringExtra("phone");

        Log.e(TAG, firebaseId);

        Picasso.with(this).load(imgUrl).into(imageView);
        namePerson.setText(name);
        if (phoneTxt != null)
            phonePerson.setText(phoneTxt);
        getRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, MissingRouteActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("long", longtude);
                startActivity(intent);
            }
        });
    }

    ProgressDialog progressBar;

    private void showMyDialog() {
        AlertDialog.Builder alert1 = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alert1.setTitle("");
        alert1.setMessage("مسح الشخص");
        alert1.setCancelable(true);
        alert1.setPositiveButton("موافق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    progressBar = new ProgressDialog(DetailActivity.this, R.style.Theme_MyDialog);
                    progressBar.setMessage(getString(R.string.loading_text));
                    progressBar.setCancelable(false);
                    progressBar.show();
                    deletePerson();

                } else {
                    Toast.makeText(DetailActivity.this, "انت لا تملك صلاحيات المسح", Toast.LENGTH_SHORT).show();

                }
            }
        });


        alert1.show();

    }

    private void deletePerson() {
        FirebaseDatabase.getInstance().getReference().child("People").child(firebaseId).removeValue();
        progressBar.dismiss();
        finish();

    }
}
