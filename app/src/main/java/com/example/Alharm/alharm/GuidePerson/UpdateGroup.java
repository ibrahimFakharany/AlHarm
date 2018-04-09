package com.example.Alharm.alharm.GuidePerson;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Alharm.alharm.Models.Groups;
import com.example.Alharm.alharm.R;
import com.example.Alharm.alharm.authentication.CustomApplication;
import com.example.Alharm.alharm.authentication.CustomSharedPreference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateGroup extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    public GoogleMap mMap;
    Location Group_Location;

    EditText group_name, group_phone;

    protected static CustomSharedPreference Pref;
    String UserID;
    private Groups group;
    private String groupID;
    private boolean isMapClicked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group);

        // تعريف ال  MapFragment حتي يتم عرض الخريطة داخلها
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);

        Group_Location = new Location("Group_Location");

        group_name = (EditText) findViewById(R.id.group_name);
        group_phone = (EditText) findViewById(R.id.group_phone);

        Pref = ((CustomApplication) getApplication()).getShared();

        UserID = Pref.getUserID();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        group = (Groups) bundle.getSerializable("Group Data");
        groupID=bundle.getString("Group ID");

        group_name.setHint(group.getGroup_name());
        group_phone.setHint(group.getGuide_phone());

        Group_Location.setLatitude(group.getLatitude());
        Group_Location.setLongitude(group.getLongitude());


    }


    // تخزين بيانات المجموعة داخل قواعد البيانات
    private void storeGroupInFirebase() {

        if (isMapClicked||!group_phone.getText().toString().isEmpty()||group_name.getText().toString().isEmpty()) {
            Groups groups = new Groups();
            //وضع بيانات المجموعة داخل object من نوع  Groups
            if (group_name.getText().toString().isEmpty())
                groups.setGroup_name(group.getGroup_name());
            else
                groups.setGroup_name(group_name.getText().toString());

            if (group_phone.getText().toString().isEmpty())
                groups.setGuide_phone(group.getGuide_phone());
            else
                groups.setGuide_phone(group_phone.getText().toString());

            groups.setLatitude(Group_Location.getLatitude());
            groups.setLongitude(Group_Location.getLongitude());

            // تحويل بيانات المجموعة الى  HashMap
            HashMap<String, Object> groupMap = groups.toMap();

            DatabaseReference Root = FirebaseDatabase.getInstance().getReference().getRoot();
            // الحصول على key جديد للمجموعة
            HashMap<String, Object> childUpdate = new HashMap<String, Object>();
            // وضع بيانات المجموعة مع المسار الذي سيتم وضع بيانات المجموعة فية داخل قواعد البيانات في  HashMap بإسم  childUpdate
            childUpdate.put("/Groups/" + UserID + "/" + groupID, groupMap);

            // وضع ال childUpdate داخل قواعد البيانات
            Root.updateChildren(childUpdate);
            Toast.makeText(this, "تم تحديث بيانات المجموعة", Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(this, "لم يتم إجراء اي تعديلات على بيانات المجموعة", Toast.LENGTH_LONG).show();


        // الانتقال الي الصفحة التى تقوم بعرض المجموعة على الخريطة
               Intent intent=new Intent(UpdateGroup.this,ManageGroups.class);
               startActivity(intent);
               UpdateGroup.this.finish();


    }


    // عندما تكون الخريطة جاهزة
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // استقبال الخريطة داخل  mMap
        mMap = googleMap;
        // تغيير قيمة المتغير  isMapReady الي true
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        markLocation();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // يتم استرجاع موفع المكان الذي تم الضغط علية وتخزينة في  Group_Location
                Group_Location.setLatitude(latLng.latitude);
                Group_Location.setLongitude(latLng.longitude);
                // اذا لم يكن موقع المجموعة فارغا يتم وضع علامة في الموقع الذي تم الضغط علية
                if (Group_Location != null) {
                    isMapClicked=true;
                    Toast.makeText(UpdateGroup.this, "تم تغيير موقع المجموعة", Toast.LENGTH_SHORT).show();
                    markLocation();
                }
                // تغيير قيمة المتغير  isMapClicked الي true والذي يعني ان المستخدم قام بالضغط على الخريطة لتحديد المكان

            }
        });
    }


    // وضع علامة على الخريطة
    public void markLocation() {
        mMap.clear();
        LatLng point = new LatLng(Group_Location.getLatitude(), Group_Location.getLongitude());
        MarkerOptions options = new MarkerOptions();
        options.position(point);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        // وضع علامة حمراء على موقع المستخدم

        // اضافة العلامات على الخريطة
        mMap.addMarker(options);

        // نحريك الكاميرا على موقع المستخدم
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        // عمل تقريب على موقع المستخدم
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


    }

    public void updateGroup(View view) {
        storeGroupInFirebase();
    }
}
