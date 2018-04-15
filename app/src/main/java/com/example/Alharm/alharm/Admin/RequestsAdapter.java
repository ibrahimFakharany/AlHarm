package com.example.Alharm.alharm.Admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.Alharm.alharm.Models.User;
import com.example.Alharm.alharm.R;

import java.util.ArrayList;

/**
 * Created by 450 G1 on 13/04/2018.
 */

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyViewHolder>{
    ArrayList<User> requests;
    LayoutInflater inflater;
    Context context;
    RequestOnClickListener listener;

    public void setListener(RequestOnClickListener listener) {
        this.listener = listener;
    }

    public interface RequestOnClickListener{

        void onRequestClick(User user);

    }

    public RequestsAdapter(ArrayList<User> requests, Context context) {
        this.requests = requests;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_request, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final User user = requests.get(position);

        holder.email.setText(user.getEmail());


        if(user.getUserType().equals("Officer")){

            holder.icon.setText("O");
            holder.icon.setBackground(context.getDrawable(R.drawable.officer_bg));

        }else{

            holder.icon.setText("G");
            holder.icon.setBackground(context.getDrawable(R.drawable.guide_bd));

        }


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRequestClick(user);
            }
        });


    }

    @Override
    public int getItemCount() {
        return requests.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView icon;
        TextView email;
        LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            email = itemView.findViewById(R.id.request_email);
            linearLayout = itemView.findViewById(R.id.linear);

        }
    }
}
