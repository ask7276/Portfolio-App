package com.example.portfolioapp.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portfolioapp.Models.User;
import com.example.portfolioapp.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserRecyclerViewHolder> {

    Context context;
    ArrayList<User> userArrayList;

    public UserAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    public class UserRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView email;

        public UserRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.user_email2);
        }
    }

    @NonNull
    @Override
    public UserRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.user_item, parent, false);
        return new UserRecyclerViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserRecyclerViewHolder holder, int position) {
        holder.email.setText("User: " + userArrayList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
}
