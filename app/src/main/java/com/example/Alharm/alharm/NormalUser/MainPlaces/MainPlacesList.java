package com.example.Alharm.alharm.NormalUser.MainPlaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Alharm.alharm.Models.Places;
import com.example.Alharm.alharm.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPlacesList extends AppCompatActivity {
    DatabaseReference PlacesRoot;
    // تعريف  ArrayList من نوع  Places لتخزين بيانات اسم المكان والموقع عند استرجاعهم من قواعد البيانات
    ArrayList<Places> placesList;
    ArrayList<String> PlacesNames;
    // تعريف  EditText التي يقوم المستخدم بالبحث عن اسم المكان باستخدامها
    EditText search;
    // تعريف object من  placesListAdapter
    placesListAdapter adapter;

    TextView PlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_places);

        placesList = new ArrayList<Places>();
        PlacesNames = new ArrayList<String>();
        search = (EditText) findViewById(R.id.search);
        PlaceName=(TextView)findViewById(R.id.PlaceName);

        Intent intent = getIntent();

        // استقبال اسم المكان الذي اختارة المستخدم من الصفحة السابقة لعرض الاماكن الرئيسية به
        String Root = intent.getStringExtra("place");
        if (Root.equals("Makkah"))
        PlaceName.setText(getString(R.string.Makkah));

        else
            PlaceName.setText(getString(R.string.Madina));

        // انشاء  DatabaseReference للإشارة على المكان الذي اختارة المستخدم لعرض الاماكن الرئيسية به
        PlacesRoot = FirebaseDatabase.getInstance().getReference().getRoot().child("Places").child(Root);

        // عرض ProgressDialog للمستخدم حتي يتم تحميل الاماكن من قواعد البيانات
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("برجاء الإنتظار .. يتم تحميل الأماكن الرئيسية من قواعد البيانات");
        progressDialog.show();

        final ListView MainPlaces = (ListView) findViewById(R.id.Mainplaces);


        // انشاء  Listener علي ال DatabaseReference لإستقبال البيانات من قواعد البيانات
        PlacesRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placesList.clear();
                PlacesNames.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    // استقبال تفاصيل الاماكن ووضعها في object من نوع  Places class والذي يحتوي على اسم المكان وموقعة
                    Places places = dataSnapshot1.getValue(Places.class);

                    // تخزين هذا ال object  داخل List من نوع Places
                    placesList.add(places);
                    // تخزين اسماء الاماكن حتي يتم عرضهم على المستخدم
                    PlacesNames.add(places.getName());
                }
                // عند الانتهاء من تحميل البيانات يتم إيقاف ال  progressDialog
                progressDialog.dismiss();
                if (!placesList.isEmpty()) {
                    // اذا لم تكن ال List التي تحتوي على بيانات المكان فارغة يتم انشاء ال  adapter لل ListView التي تعرض اسماء الاماكن
                    adapter = new placesListAdapter(PlacesNames, MainPlacesList.this);
                    MainPlaces.setAdapter(adapter);
                }
                // اذا كانت ال  List فارغة يتم اخبار المستخدم
                else
                    Toast.makeText(MainPlacesList.this, "لا يوجد بيانات في قواعد البيانات", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // عند الضغط على احد اسماء الاماكن
        MainPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              // يتم الانتقال الى صفحة جديدة تقوم بعرض موقع المكان على الخيطة وعرض مسار بين موقع المستخدم وموقع المكان
                Intent intent = new Intent(MainPlacesList.this, PlaceOnMap.class);
                Bundle bundle = new Bundle();
                TextView textView=(TextView)view.findViewById(R.id.place_name);
                String tmp=textView.getText().toString();
                // الحصول على موقع المجموعة داخل ال list
                for (int x=0;x<PlacesNames.size();x++){
                   if (tmp.equals(PlacesNames.get(x)))
                       // يتم ارسال تفاصيل اللمكان الذي قام المستخدم بالضغط علية
                       bundle.putSerializable("place Data", placesList.get(x));
               }
// يتم ارسال اسم هذة الصفحة الي الصفحة التالية ,, لاننا سوف نستخدم الصفحة التالية في اكثر من مكان لانها تقوم بنفس الوظيفة مع اختلاف فقط في اسم الصفحة التي قبلها
                bundle.putString("Activity_name", MainPlaces.class.getName());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });



        // عند الكتابة في مكان المخصص للبحث
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
               // نقوم باستدعاء فنكشن  getFilter() من placesListAdapter Clas والتي تقوم بالبحث عن اسماء الاماكن التي تشابة الاسم الذي ادخلة المستخدم في البحث وعرضها
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

    }
}
