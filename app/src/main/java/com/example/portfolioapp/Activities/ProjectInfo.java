package com.example.portfolioapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.portfolioapp.Activities.BaseClass.BaseClass;
import com.example.portfolioapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProjectInfo extends BaseClass {

    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    private FirebaseFirestore db;

    String personEmail;
    Map data = new HashMap();
    ArrayList<String> notifss;

    private TextView name, email, team, desc, proficiency, perks, members;
    Button button, b2;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);

        // fields initializations
        name = findViewById(R.id.proj_name);
        email = findViewById(R.id.proj_email);
        team = findViewById(R.id.proj_organization_name);
        desc = findViewById(R.id.proj_description);
        proficiency = findViewById(R.id.proj_proficiency);
        perks = findViewById(R.id.proj_perks);
        members = findViewById(R.id.proj_members);
        button = findViewById(R.id.proj_submit);
        b2 = findViewById(R.id.x1);

        // google initializations
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        Bundle b = getIntent().getExtras();
        String s = b.getString("Email") + " " + b.getString("Title");
        db.collection("projects").document(s)
                .get().addOnCompleteListener(task -> {
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

                    // set text to buttons according to the viewer
                    if (personEmail.equals(Objects.requireNonNull(data.get("Email")).toString())) {
                        button.setText("Update the Info");
                        b2.setText("Check the Applicants");
                    } else {
                        String applicants = Objects.requireNonNull(data.get("Applicants")).toString();
                        if (Objects.equals(data.get("Applicants"), "")) {
                            button.setText("Apply");
                        } else {
                            String[] applied = applicants.split(" ");
                            boolean ifApplied = false;
                            for (String value : applied) {
                                if (value.equals(personEmail)) {
                                    ifApplied = true;
                                    break;
                                }
                            }

                            if (ifApplied) {
                                button.setText("Applied");
                            } else {
                                button.setText("Apply");
                            }
                        }
                        b2.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Intent intent = new Intent(ProjectInfo.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        button.setOnClickListener(v -> {
            if (personEmail.equals(Objects.requireNonNull(data.get("Email")).toString())) {
                Bundle bundle = new Bundle();
                bundle.putString("Email", Objects.requireNonNull(data.get("Email")).toString());
                bundle.putString("Title", Objects.requireNonNull(data.get("Title")).toString());
                Intent intent = new Intent(ProjectInfo.this, EditProject.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                String applicants = Objects.requireNonNull(data.get("Applicants")).toString();
                if (Objects.equals(data.get("Applicants"), "")) {
                    db.collection("projects").document(Objects.requireNonNull(data.get("Email")).toString()
                            + " " + Objects.requireNonNull(data.get("Title")).toString()).update("Applicants", personEmail);
                    notifs();
                    button.setText("Applied");
                } else {
                    String[] applied = applicants.split(" ");
                    boolean ifApplied = false;
                    for (String value : applied) {
                        if (value.equals(personEmail)) {
                            ifApplied = true;
                            break;
                        }
                    }

                    if (ifApplied) {
                        Toast.makeText(ProjectInfo.this, "You have already applied", Toast.LENGTH_SHORT).show();
                    } else {
                        applicants = applicants + " " + personEmail;
                        db.collection("projects").document(Objects.requireNonNull(data.get("Email")).toString()
                                + " " + Objects.requireNonNull(data.get("Title")).toString()).update("Applicants", applicants);
                        button.setText("Applied");
                        notifs();
                    }
                }
            }
        });

        b2.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectInfo.this, ApplicantsList.class);
            Bundle bundle = new Bundle();
            bundle.putString("Email", personEmail);
            bundle.putString("Title", Objects.requireNonNull(data.get("Title")).toString());
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void notifs() {
        db.collection("users")
                .document(Objects.requireNonNull(data.get("Owner")).toString())
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {
                        db.collection("users").document(personEmail)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    DocumentSnapshot documentSnapshot1 = task1.getResult();
                                    assert documentSnapshot1 != null;
                                    if (documentSnapshot1.exists()) {
                                        notifss = (ArrayList<String>) documentSnapshot.get("Notif");
                                        String name2;
                                        name2 = (String) documentSnapshot1.get("Name");
                                        String notification = name2 + " (" + personEmail + ")" + " has applied to your project " + name.getText().toString();
                                        notifss.add(notification);
                                        if (notifss != null) {
                                            Toast.makeText(ProjectInfo.this, "vdfb", Toast.LENGTH_SHORT).show();
                                        }
                                        db.collection("users").document(Objects.requireNonNull(data.get("Owner")).toString())
                                                .update("Notif", notifss);
                                    }
                                });
                    }
                });
    }
}