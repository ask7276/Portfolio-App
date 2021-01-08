package com.example.portfolioapp.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portfolioapp.Models.Project;
import com.example.portfolioapp.R;

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectRecyclerViewHolder> {

    private Context context;
    private ArrayList<Project> userArrayList;

    public ProjectAdapter(Context context, ArrayList<Project> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProjectRecyclerViewHolder holder, int position) {

        holder.name.setText(userArrayList.get(position).getProject_Title());
        holder.email.setText("Email: " + userArrayList.get(position).getEmail());
        holder.desc.setText("Description: " + userArrayList.get(position).getDesc());
    }

    @NonNull
    @Override
    public ProjectRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.project_item, parent, false);
        return new ProjectRecyclerViewHolder(v);
    }

    public class ProjectRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView name, email, desc;

        public ProjectRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.pro_name2);
            email = itemView.findViewById(R.id.pro_email2);
            desc = itemView.findViewById(R.id.pro_desc2);
        }
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
}
