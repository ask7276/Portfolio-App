package com.example.portfolioapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portfolioapp.Models.Project;

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectRecyclerViewHolder> {

    Context context;
    ArrayList<Project> userArrayList;

    public ProjectAdapter(Context context, ArrayList<Project> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    public class ProjectRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView name, email;

        public ProjectRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.pro_name2);
            email = itemView.findViewById(R.id.pro_email2);
        }
    }

    @NonNull
    @Override
    public ProjectRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.project_item, parent, false);
        return new ProjectRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectRecyclerViewHolder holder, int position) {

        holder.name.setText(userArrayList.get(position).getProject_Title());
        holder.email.setText(userArrayList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
}
