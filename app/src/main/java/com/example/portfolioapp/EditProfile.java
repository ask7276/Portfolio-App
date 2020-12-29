package com.example.portfolioapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    EditText name,org_name, linkedin;
    CheckBox c1, c2,c3, c4,c5, c6,c7,c8;
    Button update_button;
    TextView email;
    String personEmail;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    Map<String, Object> data;

    int count=0;

    private DrawerLayout drawer;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_edit_profile);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        name = findViewById(R.id.edited_name);
        email = findViewById(R.id.edited_email);
        org_name = findViewById(R.id.edited_organization_name);
        linkedin = findViewById(R.id.edited_linkedin);
        update_button = findViewById(R.id.edit_submit);
        c1 = findViewById(R.id.edited_android_java);
        c2 = findViewById(R.id.edited_android_flutter);
        c3 = findViewById(R.id.edited_android_react);
        c4 = findViewById(R.id.edited_web_django);
        c5 = findViewById(R.id.edited_web_nodejs);
        c6 = findViewById(R.id.edited_web_frontend);
        c7 = findViewById(R.id.edited_opencv);
        c8 = findViewById(R.id.edited_nlp);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail= Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        DocumentReference usersRef;
        usersRef = db.collection("users").document(Objects.requireNonNull(personEmail));
        usersRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot docSnap = task.getResult();
                assert docSnap != null;

                if(docSnap.exists()){
                    data = docSnap.getData();
                    assert data != null;
                    name.setText(Objects.requireNonNull(data.get("Name")).toString());
                    email.setText(Objects.requireNonNull(data.get("Email")).toString());
                    linkedin.setText(Objects.requireNonNull(data.get("LinkedIn")).toString());
                    org_name.setText(Objects.requireNonNull(data.get("Organisation Name")).toString());

                }
                else{
                    startActivity(new Intent(EditProfile.this, MainActivity.class));
                }
            }
        });

        update_button.setOnClickListener(v -> updatedoc());
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
                        Toast.makeText(EditProfile.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(EditProfile.this, MainActivity.class));
                    }
            );
        } else if (item.getItemId() == R.id.user_profile_button) {
            startActivity(new Intent(EditProfile.this, ProfilePage.class));
        }

        return super.onOptionsItemSelected(item);
    }

    Map<String, Boolean> check = new HashMap<>();
    private boolean checking(){
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
            check.put("Flutter", true);count++;
        }
        if (c3.isChecked()) {
            check.put("React Native", true);count++;
        }
        if (c4.isChecked()) {
            check.put("Django", true);count++;
        }
        if (c5.isChecked()) {
            check.put("NodeJs", true);count++;
        }
        if (c6.isChecked()) {
            check.put("ReactJs", true);count++;
        }
        if (c7.isChecked()) {
            check.put("OpenCV", true);count++;
        }
        if (c8.isChecked()) {
            check.put("NLP", true);count++;
        }
        return count != 0;
    }

    public void updatedoc(){
        if(checking()){
            if(!name.getText().toString().trim().equals("")){
                data.put("Name", name.getText().toString());
            }
            if(!org_name.getText().toString().trim().equals("")){
                data.put("Organisation Name", org_name.getText().toString());
            }
            if(!linkedin.getText().toString().trim().equals("")){
                data.put("LinkedIn", linkedin.getText().toString());
            }

            data.put("checkboxes", check);
            db.collection("users").document(personEmail).set(data);
            Toast.makeText(EditProfile.this, "Updated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfile.this, HomePage.class));
        }
        else{
            Toast.makeText(EditProfile.this, "Select atleast one proficiency", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            startActivity(new Intent(EditProfile.this, HomePage.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(EditProfile.this, ProfilePage.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.nav_faq) {
            startActivity(new Intent(EditProfile.this, FaqPage.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.nav_logout) {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> {
                        Toast.makeText(EditProfile.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(EditProfile.this, MainActivity.class));
                    }
            );
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}