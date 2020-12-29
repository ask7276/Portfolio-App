package com.example.portfolioapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrationPage extends AppCompatActivity {

    EditText name;
    EditText org_name;
    EditText linkedin;

    CheckBox c1;
    CheckBox c2;
    CheckBox c3;
    CheckBox c4;
    CheckBox c5;
    CheckBox c6;
    CheckBox c7;
    CheckBox c8;

    Button select_image;
    Button submit_button;

    TextView email;
    TextView image_name;

    String personEmail;
    private static final int PICK_IMAGE_REQUEST = 1;

    String url = "";

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    public Uri mImageUri;
    private StorageReference mStorageRef;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        name = findViewById(R.id.input_name);
        org_name = findViewById(R.id.input_organization_name);
        linkedin = findViewById(R.id.input_linkedin);

        c1 = findViewById(R.id.check_android_java);
        c2 = findViewById(R.id.check_android_flutter);
        c3 = findViewById(R.id.check_android_react);
        c4 = findViewById(R.id.check_web_django);
        c5 = findViewById(R.id.check_web_nodejs);
        c6 = findViewById(R.id.check_web_frontend);
        c7 = findViewById(R.id.check_opencv);
        c8 = findViewById(R.id.check_nlp);

        select_image = findViewById(R.id.input_image);
        submit_button = findViewById(R.id.submit);

        email = findViewById(R.id.input_email);
        image_name = findViewById(R.id.input_image_name);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mStorageRef = FirebaseStorage.getInstance().getReference("ProfilePhotos");
        email.setText(mAuth.getCurrentUser().getEmail());

        select_image.setOnClickListener(v -> openFileChooser());

        submit_button.setOnClickListener(v -> uploadFile());

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            mImageUri = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(mImageUri, null, null, null, null);
            int a = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            image_name.setText(returnCursor.getString(a));
            returnCursor.close();
        }
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

    public void uploadFile() {
        if (checking() && mImageUri != null && !name.getText().toString().equals("") && !org_name.getText().toString().equals("") &&
                !linkedin.getText().toString().equals("")) {

            String personEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
            assert personEmail != null;
            StorageReference fileReference = mStorageRef.child(personEmail);
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        StorageReference downloadUriRef = FirebaseStorage.getInstance().getReference("ProfilePhotos").child(personEmail);
                        Task<Uri> downloadUriTask = downloadUriRef.getDownloadUrl();
                        while (!downloadUriTask.isSuccessful()) ;
                        Uri downloadUri = downloadUriTask.getResult();
                        assert downloadUri != null;
                        url = downloadUri.toString();

                        Map<String, Object> mdata = new HashMap<>();
                        mdata.put("Name", name.getText().toString());
                        mdata.put("Email", email.getText().toString());
                        mdata.put("Organisation Name", org_name.getText().toString());
                        mdata.put("LinkedIn", linkedin.getText().toString());
                        mdata.put("checkboxes", check);
                        mdata.put("ImageUrl", url);
                        db.collection("users").document(personEmail).set(mdata);
                        Toast.makeText(RegistrationPage.this, "Registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationPage.this, HomePage.class));
                    })
                    .addOnFailureListener(e -> Toast.makeText(RegistrationPage.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        } else {
            Toast.makeText(RegistrationPage.this, "Fill all the fields and select atleast one proficiency", Toast.LENGTH_SHORT).show();
        }
    }
}