package com.example.portfolioapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.portfolioapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfilePage extends BaseClass1 {

    private TextView name, email, org_name, linkedin, proficiency;
    private ImageView mImage;
    Button update_button;

    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;
    String personEmail;
    Map data = new HashMap();

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        org_name = findViewById(R.id.profile_organization_name);
        linkedin = findViewById(R.id.profile_linkedin);
        proficiency = findViewById(R.id.profile_proficiency);
        mImage = findViewById(R.id.profile_id);
        update_button = findViewById(R.id.update);

        // google initializations
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        // checking if person himself or some one else is viewing the profile
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String mail = b.getString("Email");
            db.collection("users").document(Objects.requireNonNull(mail))
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot docSnap = task.getResult();
                    assert docSnap != null;
                    if (docSnap.exists()) {
                        data = docSnap.getData();
                        assert data != null;
                        name.setText(Objects.requireNonNull(data.get("Name")).toString());
                        email.setText(Objects.requireNonNull(data.get("Email")).toString());
                        linkedin.setText(Objects.requireNonNull(data.get("LinkedIn")).toString());
                        org_name.setText(Objects.requireNonNull(data.get("Organisation Name")).toString());

                        String[] prof = {""};
                        Map check;
                        check = (Map) data.get("checkboxes");
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

                        String img = Objects.requireNonNull(data.get("ImageUrl")).toString();
                        if (!img.equals("")) {
                            Picasso.with(ProfilePage.this).load(img).fit().into(mImage);
                        }
                    } else {
                        startActivity(new Intent(ProfilePage.this, MainActivity.class));
                    }
                }
            });

            update_button.setVisibility(View.INVISIBLE);

        } else {
            db.collection("users").document(Objects.requireNonNull(personEmail))
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot docSnap = task.getResult();
                    assert docSnap != null;
                    if (docSnap.exists()) {
                        data = docSnap.getData();
                        assert data != null;
                        name.setText("Hi " + Objects.requireNonNull(data.get("Name")).toString());
                        email.setText(Objects.requireNonNull(data.get("Email")).toString());
                        linkedin.setText(Objects.requireNonNull(data.get("LinkedIn")).toString());
                        org_name.setText(Objects.requireNonNull(data.get("Organisation Name")).toString());

                        String[] prof = {""};
                        Map check;
                        check = (Map) data.get("checkboxes");
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
    }
}