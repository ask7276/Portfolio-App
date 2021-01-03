package com.example.portfolioapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewHolder> {

    Context context;
    ArrayList<User> userArrayList;

    public MyRecyclerViewAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.single_row, parent, false);
        return new MyRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewHolder holder, int position) {

        holder.name.setText(userArrayList.get(position).getProject_Title());
        holder.email.setText(userArrayList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
}
