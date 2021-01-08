package com.example.portfolioapp.Activities.BaseClass;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.portfolioapp.Activities.FaqPage;
import com.example.portfolioapp.Activities.HomePage;
import com.example.portfolioapp.Activities.MainActivity;
import com.example.portfolioapp.Activities.NotificationPage;
import com.example.portfolioapp.Activities.ProfilePage;
import com.example.portfolioapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class BaseClass extends AppCompatActivity {

    public DrawerLayout drawer;
    public FrameLayout frameLayout;

    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;
    String personEmail;

    @SuppressLint("InflateParams")
    @Override
    public void setContentView(int layoutResID) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        drawer = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base_class, null);
        frameLayout = (FrameLayout) drawer.findViewById(R.id.fragment_container);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        super.setContentView(drawer);
        Toolbar toolbar = drawer.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = drawer.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(BaseClass.this, HomePage.class));
                drawer.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(BaseClass.this, ProfilePage.class));
                drawer.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.nav_faq) {
                startActivity(new Intent(BaseClass.this, FaqPage.class));
                drawer.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.nav_logout) {
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        task -> {
                            Toast.makeText(BaseClass.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(BaseClass.this, MainActivity.class));
                        }
                );
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.notifications) {
            startActivity(new Intent(BaseClass.this, NotificationPage.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
