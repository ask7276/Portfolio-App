package com.example.portfolioapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class FaqPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_faq_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(FaqPage.this, HomePage.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(FaqPage.this, ProfilePage.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.nav_faq) {
            startActivity(new Intent(FaqPage.this, FaqPage.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.nav_logout) {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> {
                        Toast.makeText(FaqPage.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(FaqPage.this, MainActivity.class));
                    }
            );
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}