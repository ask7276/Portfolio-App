package com.example.portfolioapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProjectInfo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    String personEmail;
    Map data = new HashMap();

    private TextView name, email, team, desc, proficiency, perks, members;
    Button button, b2;

    private DrawerLayout drawer;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_project_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        name = findViewById(R.id.proj_name);
        email = findViewById(R.id.proj_email);
        team = findViewById(R.id.proj_organization_name);
        desc = findViewById(R.id.proj_description);
        proficiency = findViewById(R.id.proj_proficiency);
        perks = findViewById(R.id.proj_perks);
        members = findViewById(R.id.proj_members);
        button = findViewById(R.id.proj_submit);
        b2 = findViewById(R.id.x1);

        Bundle b = getIntent().getExtras();
        String s;
        s = b.getString("Email") + " " + b.getString("Title");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();


        DocumentReference usersRef;
        usersRef = db.collection("projects").document(s);
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot docSnap = task.getResult();
                assert docSnap != null;
                if (docSnap.exists()) {
                    data = docSnap.getData();
                    assert data != null;
                    name.setText(Objects.requireNonNull(data.get("Title")).toString());
                    email.setText(Objects.requireNonNull(data.get("Email")).toString());
                    members.setText(Objects.requireNonNull(data.get("Members")).toString());
                    team.setText(Objects.requireNonNull(data.get("Team")).toString());
                    desc.setText(Objects.requireNonNull(data.get("Description")).toString());
                    perks.setText(Objects.requireNonNull(data.get("Perks")).toString());

                    String[] prof = {""};
                    Map check;
                    check = (Map) data.get("Checkboxes");
                    assert check != null;
                    check.forEach((k, v) -> {
                        if (v == (Boolean) true) {
                            prof[0] = prof[0] + ", " + k.toString();
                        }
                    });
                    if (!prof[0].equals("")) {
                        prof[0] = prof[0].substring(2);
                        proficiency.setText(prof[0]);
                    }
                    if(personEmail.equals(data.get("Email").toString())){
                        button.setText("Update the Info");
                        b2.setText("Check the Applicants");
                    }
                    else{
                        button.setText("Apply");
                        b2.setVisibility(View.INVISIBLE);
                    }

                } else {
                    startActivity(new Intent(ProjectInfo.this, MainActivity.class));
                }
            }
        });


        button.setOnClickListener(v -> {
            if(personEmail.equals(data.get("Email").toString())){
                startActivity(new Intent(ProjectInfo.this, EditProject.class));
            }
            else{
                button.setText("Applied");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(ProjectInfo.this, HomePage.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(ProjectInfo.this, ProfilePage.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.nav_faq) {
            startActivity(new Intent(ProjectInfo.this, FaqPage.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.nav_logout) {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> {
                        Toast.makeText(ProjectInfo.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ProjectInfo.this, MainActivity.class));
                    }
            );
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}