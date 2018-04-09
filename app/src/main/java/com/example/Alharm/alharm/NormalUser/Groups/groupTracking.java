package com.example.Alharm.alharm.NormalUser.Groups;

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

import com.example.Alharm.alharm.Models.Groups;
import com.example.Alharm.alharm.NormalUser.MainPlaces.PlaceOnMap;
import com.example.Alharm.alharm.NormalUser.MainPlaces.placesListAdapter;
import com.example.Alharm.alharm.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class groupTracking extends AppCompatActivity {
    DatabaseReference GroupsRoot;

    ArrayList<Groups> GroupsList;
    ArrayList <String> GroupsNames;
    placesListAdapter adapter;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_group_tracking);

        GroupsRoot = FirebaseDatabase.getInstance().getReference().getRoot().child("Groups");

        GroupsList=new ArrayList<Groups>();
        GroupsNames=new ArrayList<String>();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("برجاءالإنتظار .. يتم تحميل بيانات المجموعات");
        progressDialog.show();

        final ListView MainPlaces=(ListView)findViewById(R.id.GroupsList);

        search=(EditText)findViewById(R.id.search);


        // وضع  Listener على ال GroupsRoot لاسترجاع بيانات المجموعات
        GroupsRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GroupsList.clear();
                GroupsNames.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : item.getChildren()) {
                    // استرجاع جميع بيانات المجموعات ووضعهها داخل object من نوع  Groups
                    Groups groups = dataSnapshot1.getValue(Groups.class);
                  // وضع هذا ال object داخل  GroupsList
                    GroupsList.add(groups);
                 // وضع اسماء المجموعات داخل list لعرضها علي المستخدم
                    GroupsNames.add(groups.getGroup_name());
                }
                progressDialog.dismiss();
                if (!GroupsList.isEmpty()){
                    // انشاء adapter لل listview
                    adapter=new placesListAdapter(GroupsNames,groupTracking.this);
                    MainPlaces.setAdapter(adapter );
                }else
                    Toast.makeText(groupTracking.this, "لم يتم تسجيل اي مجموعات بعد !", Toast.LENGTH_SHORT).show();

            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // عند الضغط على احد المجموعات
        MainPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(groupTracking.this,PlaceOnMap.class);
                Bundle bundle = new Bundle();
                TextView textView=(TextView)view.findViewById(R.id.place_name);
                String tmp=textView.getText().toString();
                // الحصول على موقع المجموعة داخل ال list
                for (int x=0;x<GroupsNames.size();x++){
                    if (tmp.equals(GroupsNames.get(x))) {
                        bundle.putSerializable("group Data", GroupsList.get(x));

                    }
                }
              // الانتقال الي الصفحة التالية
                bundle.putString("Activity_name", groupTracking.class.getName());
                intent.putExtras(bundle);
                startActivity(intent);



            }
        });


        // عند البحث عن احد المجموعات
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

    }
}
