package com.example.portfolioapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portfolioapp.Models.Notification;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationRecyclerViewHolder> {

    Context context;
    ArrayList<Notification> notif_list;

    public NotificationAdapter(Context context, ArrayList<Notification> notif_list) {
        this.context = context;
        this.notif_list = notif_list;
    }

    public class NotificationRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView notif;

        public NotificationRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            notif = itemView.findViewById(R.id.notification);
        }
    }

    @NonNull
    @Override
    public NotificationRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.notification_item, parent, false);
        return new NotificationRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRecyclerViewHolder holder, int position) {
        holder.notif.setText(notif_list.get(position).getNotification());
    }

    @Override
    public int getItemCount() {
        return notif_list.size();
    }
}
