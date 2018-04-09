package com.example.Alharm.alharm.NormalUser.MainPlaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Alharm.alharm.Models.Groups;
import com.example.Alharm.alharm.Models.Places;
import com.example.Alharm.alharm.Path.drawPath;
import com.example.Alharm.alharm.R;
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

//  صفحة عرض موقع المكان على الخريطة
public class PlaceOnMap extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Location destination;
    Location myLocation;
    Places place;
    Groups groups;
    GoogleApiClient mGoogleApiClient;
    SupportMapFragment mapFragment;
    public GoogleMap mMap;

    com.example.Alharm.alharm.Path.drawPath drawPath;
    boolean isMapReady=false;
    LinearLayout group_details;
    private boolean viewPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_on_map);

        TextView placeName=(TextView)findViewById(R.id.placeName);

        TextView GroupNamTextView=(TextView)findViewById(R.id.Guide_name);
        TextView guidePhoneTextView=(TextView)findViewById(R.id.Guide_phone);

        //تعريف ال layout الخاص ببيانات المجموعه لإظهارة اذا كان استخدام هذة الصفحة لعرض مكان المجموعه
        group_details=(LinearLayout)findViewById(R.id.group_details);
        Intent intent = this.getIntent( );
        Bundle bundle = intent.getExtras();

        // استقبال اسم الصفحة السابقة
        String previousActivityName=bundle.getString("Activity_name");

        // تعريف متغير من نوع Location لاستقبال موقع المكان بداخلة
        destination = new Location("destination");

        // اذا كانت الصفحة السابقة هي  MainPlaces
        if (previousActivityName.equals(MainPlaces.class.getName())) {
          // نستقبل بيانات المكان في object من نوع  Places
            // نستخدم getSerializable حتي نستطيع ارسال object من activity الى اخري
            place = (Places) bundle.getSerializable("place Data");
            // نضع تفاصيل الموقع داخل ال  destination
            destination.setLongitude(place.getLongitude());
            destination.setLatitude(place.getLatitude());
           // نضع اسم الصفحة الحالة باسم المكان الذي اختارة المستخدم لعرض الاماكن الرئيسية بة
            placeName.setText(place.getName());

        }else {
            // اذا كانت الصفحة السابقة هي  GroupTracking
            // نستقبل بيانات المكان في object من نوع  Groups
            // نستخدم getSerializable حتي نستطيع ارسال object من activity الى اخري
            groups = (Groups) bundle.getSerializable("group Data");
            // نضع تفاصيل الموقع داخل ال  destination
            destination.setLongitude(groups.getLongitude());
            destination.setLatitude(groups.getLatitude());
            // نضع  تفاصيل الحملة .. اسم المرشد ورقم هاتفة
            GroupNamTextView.setText(groups.getGroup_name());
            guidePhoneTextView.setText(groups.getGuide_phone());

            // نظهر ال Layout الخاص بتفاصيل المجموعة
            group_details.setVisibility(View.VISIBLE);
            // نضع اسم الصفحة باسم المموعة التي اختارها المستخدم
            placeName.setText(groups.getGroup_name());
        }



        // انشاء  GoogleApiClient حتي يمكننا الحصول على موقع المستخدم
        buildGoogleApiClient();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();

        // تعريف ال  MapFragment حتي يتم عرض الخريطة داخلها
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);

    }


    // عند الضغط على زر عرض المسار
    public void viewPath(View view) {
        viewPath=true;
// اذا كانت الخريطة جاهزة وموقع المستخدم غير فارغ يتم استدعاء فنكشن  drawPath  لرسم المسار
        if (isMapReady) {
            if (myLocation!=null) {
                drawPath();
            }
            else
                Toast.makeText(PlaceOnMap.this, "open your Location..", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(PlaceOnMap.this, "Map is not Ready yet ..", Toast.LENGTH_SHORT).show();
    }



    // رسم المسار
    public void drawPath() {
       // مسح البيانات الموجودة على الخريطة سابقا حتي يتم رسم المسار من جديد في كلمره يتم استدعاء هذة الفنكشن فيها
        mMap.clear();

        // الحصول على موقع المستخدم وموقع المكان ووضعهم في متغيرات من نوع  LatLng
        LatLng point1 = new LatLng(destination.getLatitude(),destination.getLongitude());
        LatLng point2 = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());

        // تعريف متغيرات من نوع  MarkerOptions حتي يتم وضع مواقع المستخدم والمكان بداخلهم لعمل علامة في مواقعهم
        MarkerOptions options1 = new MarkerOptions();
        MarkerOptions options2 = new MarkerOptions();


        // اضافة مواقع المستخدم والمكان داخل  ال MarkerOptions
        options1.position(point1);
        options2.position(point2);

        // وضع علامة زرقاء على موقع المكان الذي يبحث عنة المستخدم
        options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        // وضع علامة حمراء على موقع المستخدم
        options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

       // اضافة العلامات على الخريطة
        mMap.addMarker(options1);
        mMap.addMarker(options2);


        // تحميل بيانات الطرق بين موقع المستخدم وموقع المكان الذي يبحث عنة من  Google Directions API ورسم المسار بينهم
        String url = drawPath.getUrl(point2, point1);
        com.example.Alharm.alharm.Path.drawPath.FetchUrl FetchUrl = new drawPath.FetchUrl();
        FetchUrl.execute(url);


        // نحريك الكاميرا على موقع المستخدم
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point2));


      // عمل تقريب على موقع المستخدم
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));


    }


    // عندما تكون الخريطة جاهزة
    @Override
    public void onMapReady(GoogleMap googleMap) {
      // استقبال الخريطة داخل  mMap
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        markLocation();

        // انشاء object من ال  drawPath class وارسال ال map الية لرسم المسار عليها
        drawPath = new drawPath(mMap);
       // تغيير قيمة المتغير  isMapReady الي true
        isMapReady = true;

    }


    // وضع علامة على الخريطة
    public void markLocation() {
        mMap.clear();
        LatLng point = new LatLng(destination.getLatitude(), destination.getLongitude());
        MarkerOptions options = new MarkerOptions();
        options.position(point);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        // وضع علامة حمراء على موقع المستخدم

        // اضافة العلامات على الخريطة
        mMap.addMarker(options);

        // نحريك الكاميرا على موقع المستخدم
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        // عمل تقريب على موقع المستخدم
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


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



    // عندما يتم الاتصال ب GoogleApiClient
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // اذا لم يكن المستخدم قد سمح للبرنامج بإتسخدام خاصية تحديد الموقع يتم طلب السماح
            askPermission();
        }
// الحصول على الموقع بعد السماح للبرنامج بإستخدام خاصية تحديد الموقع على الهاتف
        myLocation  = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
    private final int UPDATE_INTERVAL = 20000;
    private final int FASTEST_INTERVAL = 5000;

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
        // يتم تحديث موقع المستخدم بالموقع الجديد
        myLocation =location;
        if (myLocation!=null&&destination!=null)
            if (viewPath)
            drawPath();


    }


}
