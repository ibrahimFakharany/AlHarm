package com.example.Alharm.alharm.MissingPersone;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.Alharm.alharm.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MissingPeopleAdapter extends RecyclerView.Adapter<MissingPeopleAdapter.MyViewHolder> {
    Context context;
    ArrayList<MissingPersonModel> personList;
    LayoutInflater inflater;

    public MissingPeopleAdapter(Context context, ArrayList<MissingPersonModel> personList) {
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
        final MissingPersonModel person = personList.get(position);
        Picasso.with(context).load(person.getImgUrl()).into(holder.personImage);
        holder.personName.setText(person.getName());

        if (person.getConfidence() != -1) {
            holder.confidence.setVisibility(View.VISIBLE);
            int confidence = (int) (person.getConfidence() * 100);
            holder.confidence.setText(confidence + "%");

        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("name", person.getName());
                intent.putExtra("imgUrl", person.getImgUrl());
                intent.putExtra("lat", person.getLat());
                intent.putExtra("long", person.getLang());
                intent.putExtra("firebaseId", person.getFirebaseKey());
                if (person.getPhone() != null && person.getPhone().length() > 0) {

                    intent.putExtra("phone", person.getPhone());

                }
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView personImage;
        TextView personName;
        TextView confidence;
        LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            personImage = itemView.findViewById(R.id.person_image);
            personName = itemView.findViewById(R.id.person_name);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            confidence = itemView.findViewById(R.id.confidence_txt);
        }
    }
}
