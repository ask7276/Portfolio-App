package com.example.portfolioapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button); // button to sign in
        mAuth = FirebaseAuth.getInstance();
        btnSignOut = findViewById(R.id.sign_out_button); // button to sign out

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(v -> signIn());

        btnSignOut.setOnClickListener(v -> {
            mGoogleSignInClient.signOut();
            Toast.makeText(MainActivity.this, "You are Logged Out", Toast.LENGTH_SHORT).show();
            btnSignOut.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);
        });
    }

    // called automatically when activity starts after onCreate
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser); // updates the UI for the signed in users
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(MainActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                FirebaseUser user = mAuth.getCurrentUser();
                Map<String, String> data = new HashMap<>();
                assert user != null;
                data.put("Email", user.getEmail());
                data.put("Name", user.getDisplayName());
                data.put("ImageUrl", "");

                DocumentReference userRef = db.collection("users").document(Objects.requireNonNull(user.getEmail()));
                userRef.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot document = task1.getResult();
                        assert document != null;
                        if (!document.exists()) {
                            db.collection("users").document(Objects.requireNonNull(user.getEmail())).set(data);
                        }
                    } else {
                        db.collection("users").document(Objects.requireNonNull(user.getEmail())).set(data);
                    }
                });
                updateUI(user);
            } else {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(FirebaseUser fUser) {
        signInButton.setVisibility(View.INVISIBLE);
        if (fUser != null) {
            String personEmail = fUser.getEmail();
            Toast.makeText(MainActivity.this, personEmail, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, HomePage.class);
//            startActivity(intent);
        }
    }
}


