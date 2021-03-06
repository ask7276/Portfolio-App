package com.example.portfolioapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class NewProject extends BaseClass {

    EditText title, org_team, desc, perks, members;
    TextView email;
    CheckBox c1, c2, c3, c4, c5, c6, c7, c8;
    Button button;

    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;

    String personEmail;
    int count = 0;
    Map<String, Boolean> check = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        // fields initializations
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

        // google initializations
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        email.setText(personEmail);

        button.setOnClickListener(v -> submit());
    }

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
                !desc.getText().toString().equals("") && !perks.getText().toString().equals("") && checking()) {
            Map<String, Object> mdata = new HashMap<>();
            mdata.put("Title", title.getText().toString());
            mdata.put("Email", email.getText().toString());
            mdata.put("Team", org_team.getText().toString());
            mdata.put("Description", desc.getText().toString());
            mdata.put("Perks", perks.getText().toString());
            mdata.put("Members", members.getText().toString());
            mdata.put("Checkboxes", check);
            mdata.put("Applicants", "");
            db.collection("projects").document(email.getText().toString() + " " + title.getText().toString()).set(mdata);

            // adding notification to relevant profiles
            db.collection("users")
                    .get()
                    .addOnCompleteListener(task -> {
                        for (DocumentSnapshot querySnapshot : Objects.requireNonNull(task.getResult())) {
                            Map<String, Boolean> check2 = (Map<String, Boolean>) querySnapshot.get("checkboxes");
                            assert check2 != null;
                            for (String key : check2.keySet()) {
                                if (check2.get(key) == (Boolean) true && check.get(key) && !personEmail.equals(querySnapshot.getString("Email"))) {
                                    String notification;
                                    notification = "New project named " + title.getText().toString()
                                            + " was posted with requirements matching your skills";
                                    ArrayList<String> notif = (ArrayList<String>) querySnapshot.get("Notif");
                                    assert notif != null;
                                    notif.add(notification);
                                    db.collection("users")
                                            .document(Objects.requireNonNull(querySnapshot.getString("Email")))
                                            .update("Notif", notif);
                                    break;
                                }
                            }
                        }
                    });


            startActivity(new Intent(NewProject.this, HomePage.class));
        } else {
            Toast.makeText(NewProject.this, "Fill all the fields and select atleast one proficiency", Toast.LENGTH_SHORT).show();
        }
    }
}