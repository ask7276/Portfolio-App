package com.example.portfolioapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfilePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView name, email, org_name, linkedin, proficiency;
    ImageView mImage;
    Button update_button;

    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;

    String personEmail;
    Map data = new HashMap();

    private DrawerLayout drawer;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_profile_page);


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


        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        org_name = findViewById(R.id.profile_organization_name);
        linkedin = findViewById(R.id.profile_linkedin);
        proficiency = findViewById(R.id.profile_proficiency);

        mImage = findViewById(R.id.profile_id);

        update_button = findViewById(R.id.update);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        DocumentReference usersRef;
        usersRef = db.collection("users").document(Objects.requireNonNull(personEmail));
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot docSnap = task.getResult();
                assert docSnap != null;
                if (docSnap.exists()) {
                    data = docSnap.getData();
                    assert data != null;
                    name.setText("Hi "+ Objects.requireNonNull(data.get("Name")).toString());
                    email.setText(Objects.requireNonNull(data.get("Email")).toString());
                    linkedin.setText(Objects.requireNonNull(data.get("LinkedIn")).toString());
                    org_name.setText(Objects.requireNonNull(data.get("Organisation Name")).toString());

                    String[] prof = {""};
                    Map check;
                    check = (Map) data.get("checkboxes");
                    assert check != null;
                    check.forEach((k, v) -> {
                        if (v == (Boolean) true) {
                            prof[0] = prof[0] + "," + k.toString();
                        }
                    });
                    if (!prof[0].equals("")) {
                        prof[0] = prof[0].substring(1);
                        proficiency.setText(prof[0]);
                    }
                    String img = Objects.requireNonNull(data.get("ImageUrl")).toString();
                    if (!img.equals("")) {
                        Picasso.with(ProfilePage.this).load(img).fit().into(mImage);
                    }
                } else {
                    startActivity(new Intent(ProfilePage.this, MainActivity.class));
                }
            }
        });

        update_button.setOnClickListener(v -> startActivity(new Intent(ProfilePage.this, EditProfile.class)));

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
                        Toast.makeText(ProfilePage.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ProfilePage.this, MainActivity.class));
                    }
            );
        } else if (item.getItemId() == R.id.user_profile_button) {
            startActivity(new Intent(ProfilePage.this, ProfilePage.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(ProfilePage.this, HomePage.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(ProfilePage.this, ProfilePage.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.nav_faq) {
            startActivity(new Intent(ProfilePage.this, FaqPage.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.nav_logout) {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> {
                        Toast.makeText(ProfilePage.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ProfilePage.this, MainActivity.class));
                    }
            );
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}