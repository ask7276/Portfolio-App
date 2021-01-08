package com.example.portfolioapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portfolioapp.Activities.BaseClass.BaseClass;
import com.example.portfolioapp.Models.User;
import com.example.portfolioapp.R;
import com.example.portfolioapp.RecyclerItemClickListener.RecyclerItemClickListener;
import com.example.portfolioapp.RecyclerViewAdapter.UserAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ApplicantsList extends BaseClass {

    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;

    String personEmail;

    RecyclerView mRecyclerView;
    UserAdapter adapter;
    ArrayList<User> userArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants_list);

        // google initializations
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        //recycler view initializations
        userArrayList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.user_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (userArrayList.size() > 0)
            userArrayList.clear();

        Bundle b = getIntent().getExtras();
        String s = b.getString("Email") + " " + b.getString("Title");

        final String[][] appli = new String[1][];
        db.collection("projects")
                .document(s)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        assert documentSnapshot != null;
                        String applicants = Objects.requireNonNull(documentSnapshot.get("Applicants")).toString();
                        appli[0] = applicants.split(" ");
                        for (String mail : appli[0]) {
                            User user = new User(mail);
                            userArrayList.add(user);
                        }
                        adapter = new UserAdapter(ApplicantsList.this, userArrayList);
                        mRecyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ApplicantsList.this, "Failed", Toast.LENGTH_SHORT).show());

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle b = new Bundle();
                        b.putString("Email", userArrayList.get(position).getEmail());
                        Intent intent = new Intent(ApplicantsList.this, ProfilePage.class);
                        intent.putExtras(b);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
    }
}