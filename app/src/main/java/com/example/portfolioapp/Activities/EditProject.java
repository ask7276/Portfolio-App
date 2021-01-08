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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProject extends BaseClass {

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    String personEmail;
    Map data = new HashMap();

    TextView name;
    EditText email, team, desc, perks, members;
    Button button;
    CheckBox c1, c2, c3, c4, c5, c6, c7, c8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        name = findViewById(R.id.proj_name2);
        email = findViewById(R.id.proj_email2);
        team = findViewById(R.id.proj_organization_name2);
        desc = findViewById(R.id.proj_description2);
        perks = findViewById(R.id.proj_perks2);
        members = findViewById(R.id.proj_members2);
        button = findViewById(R.id.proj_submit2);

        c1 = findViewById(R.id.project_java2);
        c2 = findViewById(R.id.project_flutter2);
        c3 = findViewById(R.id.project_react2);
        c4 = findViewById(R.id.project_django2);
        c5 = findViewById(R.id.project_nodejs2);
        c6 = findViewById(R.id.project_frontend2);
        c7 = findViewById(R.id.project_opencv2);
        c8 = findViewById(R.id.project_nlp2);

        Bundle b = getIntent().getExtras();
        String s;
        s = b.getString("Email") + " " + b.getString("Title");

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
                }
            }
        });

        button.setOnClickListener(v -> submit());
    }

    int count = 0;
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
        if (!name.getText().toString().equals("") && !email.getText().toString().equals("") && !team.getText().toString().equals("") &&
                !desc.getText().toString().equals("") && !perks.getText().toString().equals("") && !members.getText().toString().equals("") && checking()) {
            Map<String, Object> mdata = new HashMap<>();
            mdata.put("Title", name.getText().toString());
            mdata.put("Email", email.getText().toString());
            mdata.put("Team", team.getText().toString());
            mdata.put("Description", desc.getText().toString());
            mdata.put("Perks", perks.getText().toString());
            mdata.put("Members", members.getText().toString());
            mdata.put("Checkboxes", check);
            mdata.put("Owner", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
            mdata.put("Applicants", data.get("Applicants"));
            db.collection("projects").document(email.getText().toString() + " " + name.getText().toString()).set(mdata);
            startActivity(new Intent(EditProject.this, HomePage.class));
        } else {
            Toast.makeText(EditProject.this, "Fill all the fields and select atleast one proficiency", Toast.LENGTH_SHORT).show();
        }
    }
}