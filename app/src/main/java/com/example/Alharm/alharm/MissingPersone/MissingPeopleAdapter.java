package com.example.Alharm.alharm.MissingPersone;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Alharm.alharm.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MissingPeopleAdapter extends RecyclerView.Adapter<MissingPeopleAdapter.MyViewHolder>{
    Context context;
    ArrayList<MissingPersonModel> personList;
    LayoutInflater inflater;
    public MissingPeopleAdapter(Context context,ArrayList<MissingPersonModel> personList ){
        this.context = context;
        this.personList = personList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_missing_person, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MissingPersonModel person = personList.get(position);
        Picasso.with(context).load(person.getImgUrl()).into(holder.personImage);
            holder.personName.setText(person.getName());

            if(person.getConfidence() != -1){

            holder.confidence.setVisibility(View.VISIBLE);
            int confidence = (int) (person.getConfidence() * 100);
            holder.confidence.setText(confidence+"%");

        }
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView personImage;
        TextView personName;
        TextView confidence;

        public MyViewHolder(View itemView) {
            super(itemView);
            personImage = itemView.findViewById(R.id.person_image);
            personName = itemView.findViewById(R.id.person_name);
            confidence = itemView.findViewById(R.id.confidence_txt);
        }
    }
}
