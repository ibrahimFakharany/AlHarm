package com.example.Alharm.alharm.GuidePerson;


import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Alharm.alharm.Models.Groups;
import com.example.Alharm.alharm.R;

import java.util.ArrayList;

public class GroupsListAdapter  extends BaseAdapter {
    ArrayList<Groups> Groups;
    Context context;
    public GroupsListAdapter(ArrayList<Groups> Groups, Context context){
        this.Groups=Groups;
        this.context=context;
    }
    @Override
    public int getCount() {
        return Groups.size();
    }

    @Override
    public Object getItem(int i) {
        return Groups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.groups_list, null);
        }
        TextView name = (TextView) v.findViewById(R.id.group_name);
        TextView phone = (TextView) v.findViewById(R.id.group_phone);

        final String mPhone=Groups.get(i).getGuide_phone();
        final String mName=Groups.get(i).getGroup_name();

        name.setText(mName);
        phone.setText( mPhone);

        /*ImageView delete=(ImageView) v.findViewById(R.id.delete);
        ImageView edit=(ImageView) v.findViewById(R.id.edit);*/
        final ImageView info=(ImageView) v.findViewById(R.id.info);


        // عند الضغط على صورة حزف المجموعة
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(context, info);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.group_button_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id){
                            case R.id.edit:
                                if(context instanceof ManageGroups){
                                    ((ManageGroups)context).updateGroup(mName,Groups.get(i));
                                }
                            break;
                            case R.id.delete:
                                if(context instanceof ManageGroups){
                                    ((ManageGroups)context).removeGroup( mName);
                                }
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        // عند الضغط على صورة تعديل المجموعة
        /*edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        return v;

    }

}
