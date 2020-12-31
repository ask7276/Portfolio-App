package com.example.portfolioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewProject extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText title, email, org_team, desc, perks, members;
    CheckBox c1, c2, c3, c4, c5, c6, c7, c8;
    Button button;

    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;

    String personEmail;

    private DrawerLayout drawer;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_new_project);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        title = findViewById(R.id.project_name);
        email = findViewById(R.id.project_email);
        org_team = findViewById(R.id.project_organization_name);
        desc = findViewById(R.id.project_description);
        perks = findViewById(R.id.project_perks);
        members = findViewById(R.id.project_members);

        c1 = findViewById(R.id.project_input_android_java);
        c2 = findViewById(R.id.project_input_android_flutter);
        c3 = findViewById(R.id.project_input_android_react);
        c4 = findViewById(R.id.project_input_web_django);
        c5 = findViewById(R.id.project_input_web_nodejs);
        c6 = findViewById(R.id.project_input_web_frontend);
        c7 = findViewById(R.id.project_input_opencv);
        c8 = findViewById(R.id.project_input_nlp);

        button = findViewById(R.id.project_submit);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        button.setOnClickListener(v -> submit());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.sign_out_button) {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> {
                        Toast.makeText(NewProject.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(NewProject.this, MainActivity.class));
                    }
            );
        } else if (item.getItemId() == R.id.user_profile_button) {
            startActivity(new Intent(NewProject.this, ProfilePage.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(NewProject.this, HomePage.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(NewProject.this, ProfilePage.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.nav_faq) {
            startActivity(new Intent(NewProject.this, FaqPage.class));
            drawer.closeDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.nav_logout) {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> {
                        Toast.makeText(NewProject.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(NewProject.this, MainActivity.class));
                    }
            );
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    Map<String, Boolean> check = new HashMap<>();

    private boolean checking() {
        check.put("Java/Kotlin", false);
        check.put("Flutter", false);
        check.put("React Native", false);
        check.put("Django", false);
        check.put("NodeJs", false);
        check.put("ReactJs", false);
        check.put("OpenCV", false);
        check.put("NLP", false);
        if (c1.isChecked()) {
            check.put("Java/Kotlin", true);
            count++;
        }
        if (c2.isChecked()) {
            check.put("Flutter", true);
            count++;
        }
        if (c3.isChecked()) {
            check.put("React Native", true);
            count++;
        }
        if (c4.isChecked()) {
            check.put("Django", true);
            count++;
        }
        if (c5.isChecked()) {
            check.put("NodeJs", true);
            count++;
        }
        if (c6.isChecked()) {
            check.put("ReactJs", true);
            count++;
        }
        if (c7.isChecked()) {
            check.put("OpenCV", true);
            count++;
        }
        if (c8.isChecked()) {
            check.put("NLP", true);
            count++;
        }
        return count != 0;
    }

    private void submit() {
        if (!title.getText().toString().equals("") && !email.getText().toString().equals("") && !org_team.getText().toString().equals("") &&
                !desc.getText().toString().equals("") && !perks.getText().toString().equals("") && !members.getText().toString().equals("") && checking()) {
            Map<String, Object> mdata = new HashMap<>();
            mdata.put("Project Title", title.getText().toString());
            mdata.put("Email", email.getText().toString());
            mdata.put("Organizing Team", org_team.getText().toString());
            mdata.put("Description", desc.getText().toString());
            mdata.put("Perks", perks.getText().toString());
            mdata.put("Existing Team Members", members.getText().toString());
            mdata.put("Checkboxes", check);
            mdata.put("Owner", mAuth.getCurrentUser().getEmail());
            db.collection("projects").document(email.getText().toString() + " " + title.getText().toString()).set(mdata);
            startActivity(new Intent(NewProject.this, HomePage.class));
        } else {
            Toast.makeText(NewProject.this, "Fill all the fields and select atleast one proficiency", Toast.LENGTH_SHORT).show();
        }
    }
}