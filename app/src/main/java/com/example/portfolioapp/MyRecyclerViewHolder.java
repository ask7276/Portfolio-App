package com.example.portfolioapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.TaskExecutor;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView name, email;

    public MyRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.pro_name2);
        email = itemView.findViewById(R.id.pro_email2);
    }
}
