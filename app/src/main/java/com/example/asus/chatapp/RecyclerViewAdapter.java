package com.example.asus.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    Context context;
    ArrayList<User> profiles;
    private Context mContext;

    public RecyclerViewAdapter(Context c, ArrayList<User> p) {
        context = c;
        profiles = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.single_user_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.name.setText(profiles.get(position).getName());
        holder.status.setText(profiles.get(position).getStatus());
        Picasso.get().load(profiles.get(position).getThumbImage()).placeholder(R.drawable.default_avatar).into(holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        ImageView profilePic;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            status = itemView.findViewById(R.id.user_status);
            profilePic = itemView.findViewById(R.id.user_img);

        }
    }

}
