package com.example.Alharm.alharm.GuidePerson;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.Alharm.alharm.Models.Groups;
import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.NormalUser.Groups.groupTracking;
import com.example.Alharm.alharm.NormalUser.MainPlaces.PlaceOnMap;
import com.example.Alharm.alharm.R;
import com.example.Alharm.alharm.authentication.CustomApplication;
import com.example.Alharm.alharm.authentication.CustomSharedPreference;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class CreateGroup extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    RelativeLayout mapLayout;
    ScrollView mainLayout;
    GoogleApiClient mGoogleApiClient;
    SupportMapFragment mapFragment;
    public GoogleMap mMap;
    private boolean isMapReady = false;


    EditText group_name, group_phone;
    Location Group_Location;
    Location myLocation;
    User user;

    boolean isMapClicked = false;
    protected static CustomSharedPreference Pref;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Group_Location = new Location("Group_Location");


        // انشاء  GoogleApiClient حتي يمكننا الحصول على موقع المستخدم
        buildGoogleApiClient();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        user = (User) bundle.getSerializable("User Data");

        Pref = ((CustomApplication) getApplication()).getShared();

        UserID = Pref.getUserID();

        // تعريف ال  MapFragment حتي يتم عرض الخريطة داخلها
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);

        group_name = (EditText) findViewById(R.id.group_name);
        group_phone = (EditText) findViewById(R.id.group_phone);

        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        mainLayout = (ScrollView) findViewById(R.id.mainLayout);


    }


    // عند الضغط على زر تحديد موقع المجموعة
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void defineGroupLocation(View view) {
        // يتم اظهار الخريطة
        mapLayout.setVisibility(View.VISIBLE);
        final Drawable drawable = new ColorDrawable(getResources().getColor(R.color.forground));
        mainLayout.setForeground(drawable);

    }

    // عند الضغط على زر انشاء المجموعة
    public void createGroup(View view) {
        // اذا تم تحديد موقع المجموعة
        if (Group_Location != null && Group_Location.getLatitude() != 0) {
            // اذا كانت البيانات المطلوبة قد تم ادخالها من قبل المستخدم
            if (!group_name.getText().toString().isEmpty() && !group_phone.getText().toString().isEmpty()) {
                // يتم استدعاء فنكشن التخزين داخل قواعد البيانات
                storeGroupInFirebase();
            } else
                Toast.makeText(this, "يجب ادخال جميع البيانات المطلوبة", Toast.LENGTH_LONG).show();
        }
        // اذا لم يكن المستخدم قد قام بتسجيل موقع المجموعة بعد او حدث خطأ ما
        else
            Toast.makeText(this, "لم يتم تحديد موقع المجموعة .. برجاء المحاولة مرة اخري ", Toast.LENGTH_LONG).show();


    }


    // تخزين بيانات المجموعة داخل قواعد البيانات
    private void storeGroupInFirebase() {
        Groups groups = new Groups();
        //وضع بيانات المجموعة داخل object من نوع  Groups
        groups.setGroup_name(group_name.getText().toString());
        groups.setGuide_phone(group_phone.getText().toString());
        groups.setLatitude(Group_Location.getLatitude());
        groups.setLongitude(Group_Location.getLongitude());

        // تحويل بيانات المجموعة الى  HashMap
        HashMap<String, Object> groupMap = groups.toMap();

        DatabaseReference Root = FirebaseDatabase.getInstance().getReference().getRoot();
        // الحصول على key جديد للمجموعة
        String temp_key = Root.child("Groups").child(UserID).push().getKey();
        HashMap<String, Object> childUpdate = new HashMap<String, Object>();
        // وضع بيانات المجموعة مع المسار الذي سيتم وضع بيانات المجموعة فية داخل قواعد البيانات في  HashMap بإسم  childUpdate
        childUpdate.put("/Groups/" + UserID + "/" + temp_key, groupMap);

        // وضع ال childUpdate داخل قواعد البيانات
        Root.updateChildren(childUpdate);

        group_name.setText("");
        group_phone.setText("");

        // الانتقال الي الصفحة التى تقوم بعرض المجموعة على الخريطة
        Intent intent = new Intent(CreateGroup.this, PlaceOnMap.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("group Data", groups);
        bundle.putString("Activity_name", groupTracking.class.getName());
        intent.putExtras(bundle);

        startActivity(intent);

    }


    // عند الضغط على زر تم
    public void done(View view) {
        // اذا تم تحديد موقع المجموعة
        if (Group_Location != null && Group_Location.getLatitude() != 0) {
            // يتم اخفاء الخريطة
            mapLayout.setVisibility(View.GONE);
            final Drawable drawable = new ColorDrawable(getResources().getColor(R.color.forground2));
            mainLayout.setForeground(drawable);

        } else {
            Toast.makeText(this, "يجب تحديد موقع المجموعة", Toast.LENGTH_LONG).show();

        }

    }


    // انشاء  GoogleApiClient
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    // عندما تكون الخريطة جاهزة
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // استقبال الخريطة داخل  mMap
        mMap = googleMap;
        // تغيير قيمة المتغير  isMapReady الي true
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        isMapReady = true;
        if (checkPermission())
            mMap.setMyLocationEnabled(true);
        // عند الضغط على الخريطة
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // يتم استرجاع موفع المكان الذي تم الضغط علية وتخزينة في  Group_Location
                Group_Location.setLatitude(latLng.latitude);
                Group_Location.setLongitude(latLng.longitude);
                // اذا لم يكن موقع المجموعة فارغا يتم وضع علامة في الموقع الذي تم الضغط علية
                if (Group_Location != null)
                    markLocation();
                // تغيير قيمة المتغير  isMapClicked الي true والذي يعني ان المستخدم قام بالضغط على الخريطة لتحديد المكان
                isMapClicked = true;


            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // اذا لم يكن المستخدم قد سمح للبرنامج بإتسخدام خاصية تحديد الموقع يتم طلب السماح
            askPermission();
        }
// الحصول على الموقع بعد السماح للبرنامج بإستخدام خاصية تحديد الموقع على الهاتف
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        LatLng point;
        if (myLocation != null) {
            point = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            if (isMapReady)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        }
        // بدأ تحديث طلب الموقع
        startLocationUpdates();
    }

    // طلب السماح للبرنامج باستخدام خاصية الموقع
    private void askPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }

    // التحقق من رد المستخدم على طلب السماح باستخدام خاصية الموقع
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
// اذا قام المستخدم برفض السماح للبرنامج باستخدام الموقع
                    // يتم استدعاء function  permissionsDenied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // البرنامج لا يمكنة العمل بدون ان يسمح له المستخدم باستخدام الموقع
    private void permissionsDenied() {
        Toast.makeText(this, "You Must allow Location permission .. ", Toast.LENGTH_SHORT).show();
        // لذلك يتكرر طلب السماح باستخدام الموقع من المستخدم حتي يتم الموافقة
        askPermission();
    }


    private final int REQ_PERMISSION = 999;

    // التحقق من وجود السماح للتطبيق باستخدام الموقع
    private boolean checkPermission() {
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private LocationRequest locationRequest;
    private final int UPDATE_INTERVAL = 120000;
    private final int FASTEST_INTERVAL = 10000;

    // عمل تحديث للموقع للتتبع
    private void startLocationUpdates() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setSmallestDisplacement(1);

        if (checkPermission())
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // عند تغير موقع المستخدم
    @Override
    public void onLocationChanged(Location location) {
// اذا لم يكن المستخدم قد قام بالضغط على الخريطة بعد
        if (!isMapClicked) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            if (isMapReady)
                // تحريك الكاميرا الي موقع المستخدم
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

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

}
