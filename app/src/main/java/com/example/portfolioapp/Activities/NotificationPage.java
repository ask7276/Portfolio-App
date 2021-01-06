package com.example.portfolioapp.Activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portfolioapp.Models.Notification;
import com.example.portfolioapp.NotificationAdapter;
import com.example.portfolioapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class NotificationPage extends BaseClass1 {

    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;

    String personEmail;

    RecyclerView mRecyclerView;
    ArrayList<Notification> notificationArrayList;
    NotificationAdapter adapter;
    ArrayList<String> notifs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);

        // google initializations
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        // recycler view initializations
        notificationArrayList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.notification_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (notificationArrayList.size()>0) {
            notificationArrayList.clear();
        }
        notifs = new ArrayList<>();
        notifs.add("normal notif");
        db.collection("users")
                .document(personEmail)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    if(documentSnapshot.exists()){
                        Map m = documentSnapshot.getData();
                        assert m != null;
                        notifs = (ArrayList<String>) m.get("Notif");
                        assert notifs != null;
                        for(String note: notifs){
                            Notification notification = new Notification(note);
                            notificationArrayList.add(notification);
                        }
                        if(notificationArrayList.size() > 0){
                            adapter = new NotificationAdapter(NotificationPage.this, notificationArrayList);
                            mRecyclerView.setAdapter(adapter);
                        }
                    }
                });
    }
}

