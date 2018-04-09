package com.example.Alharm.alharm.GuidePerson;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Alharm.alharm.Models.Groups;
import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.R;
import com.example.Alharm.alharm.authentication.CustomApplication;
import com.example.Alharm.alharm.authentication.CustomSharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ManageGroups extends AppCompatActivity {
  //صفحة ادارة المجموعات

    DatabaseReference GroupsRoot;

    ArrayList<Groups> GroupsList;
    ArrayList <String> GroupsNames;

    protected static Gson mGson;
    protected static CustomSharedPreference mPref;
    private static User mUser;
    private static String userString;
    String groupID;


    String UserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_groups);


        GroupsList=new ArrayList<Groups>();
        GroupsNames=new ArrayList<String>();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("برجاءالإنتظار .. يتم تحميل بيانات المجموعات");
        progressDialog.show();


        mGson = ((CustomApplication)getApplication()).getGsonObject();
        mPref = ((CustomApplication)getApplication()).getShared();

        UserID=mPref.getUserID();
       // الجصول على بيانات المستخدم من ال  SharedPreference
        userString = mPref.getUserData();
        mUser = mGson.fromJson(userString, User.class);

        final ListView GroupsListView=(ListView)findViewById(R.id.GroupsList);

        GroupsRoot = FirebaseDatabase.getInstance().getReference().getRoot().child("Groups").child(UserID);

        // وضع  Listener على ال GroupsRoot لاسترجاع بيانات المجموعات
        GroupsRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GroupsList.clear();
                GroupsNames.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Groups groups = dataSnapshot1.getValue(Groups.class);
                        GroupsList.add(groups);
                        GroupsNames.add(groups.getGroup_name());

                    }
                progressDialog.dismiss();
                if (GroupsList.isEmpty())
                    Toast.makeText(ManageGroups.this, "لم يتم تسجيل اي مجموعات بعد !", Toast.LENGTH_SHORT).show();

                // وضع  Adapter لل listView
                    GroupsListView.setAdapter(new GroupsListAdapter(GroupsList,ManageGroups.this));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    // عند الضغط على انشاء مجموعة جديدة
    public void createNewGroup(View view) {
    // يتم الانتقال الي صفحة انشاء مجموعة
        Bundle bundle = new Bundle();
        bundle.putSerializable("User Data",mUser);
        Intent intent =new Intent(ManageGroups.this,CreateGroup.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


   // عند الضغط على صورة حزف مجموعة
    public void removeGroup(final String mName) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
      // يتم استرجاع المجموعات التي تحتوي على رقم هاتف مطابق لرقم هاتف المستخدم الحالي
        Query applesQuery = ref.child("Groups").child(UserID).orderByChild("group_name").equalTo(mName);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                      // عند ايجاد المجموعة يتم حزفها من قواعد البيانات
                        Snapshot.getRef().removeValue();
                        Toast.makeText(ManageGroups.this, "تم مسح المجموعة بنجاح", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void updateGroup(String group_name, final Groups groupItem){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        // يتم استرجاع المجموعات التي تحتوي على رقم هاتف مطابق لرقم هاتف المستخدم الحالي
        Query applesQuery = ref.child("Groups").child(UserID).orderByChild("group_name").equalTo(group_name);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    groupID=Snapshot.getKey();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Group Data",groupItem);
                    bundle.putString("Group ID",groupID);
                    Intent intent=new Intent(ManageGroups.this,UpdateGroup.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
