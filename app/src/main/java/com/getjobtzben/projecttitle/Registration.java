package com.getjobtzben.projecttitle;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.getjobtzben.projecttitle.model.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Registration extends AppCompatActivity {

    private RoundedImageView businessProfile1;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FirebaseApp.initializeApp(this);

        EditText name = findViewById(R.id.jinalako);
        EditText email = findViewById(R.id.emailyako);
        EditText password = findViewById(R.id.passwordyako);
        TextView error = findViewById(R.id.error);
        TextView ingia = findViewById(R.id.ingia);
        businessProfile1 = findViewById(R.id.businessProfile1);
        Button register = findViewById(R.id.jisajili);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference("userProfile");

        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        if (data != null && data.getData() != null) {
                            // Get the selected image URI
                            selectedImageUri = result.getData().getData();

                            Glide.with(Registration.this)
                                    .load(selectedImageUri)
                                    .apply(RequestOptions.centerCropTransform())
                                    .into(businessProfile1);

                            businessProfile1.setImageURI(selectedImageUri);

                        }
                    }
                }
        );

        businessProfile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(Registration.this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickerLauncher.launch(intent);
                                return null;
                            }
                        });
            }
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String getName = name.getText().toString().trim();
              String getEmail= email.getText().toString().trim();
              String getPassword = password.getText().toString().trim();

                if (getName.isEmpty()) {
                    name.setError("Enter name");
                    name.requestFocus();
                    return;
                }

                if (getEmail.isEmpty()) {
                    email.setError("Enter email");
                    email.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
                    email.setError("Invalid email!");
                    email.requestFocus();
                    return;
                }

                if (getPassword.isEmpty()) {
                    password.setError("Enter password");
                    password.requestFocus();
                    return;
                }

                if (getPassword.length() < 8) {
                    password.setError("Eight characters required!");
                    password.requestFocus();
                    return;
                }

                ProgressDialog dialog = new ProgressDialog(Registration.this);
                dialog.setMessage("Please wait...");
                dialog.show();

                if (selectedImageUri != null) {

                    StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImageUri));

                    mAuth.fetchSignInMethodsForEmail(getEmail)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    List<String> signInMethods = task.getResult().getSignInMethods();
                                    if (signInMethods != null && !signInMethods.isEmpty()) {
                                        // Email is already registered
                                        // signInMethods list will contain the sign-in methods associated with this email
                                        // For example, if the email is registered with password authentication,
                                        // signInMethods will contain "password"
                                        // You can handle this case here
                                        Toast.makeText(Registration.this, "email is already taken, try another email", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        // Email is not registered
                                        // You can handle this case here
                                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(getEmail, getPassword)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                                        if (task.isSuccessful()){
                                                            // User registered successfully
                                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                            fileReference.putFile(selectedImageUri)
                                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                @Override
                                                                                public void onSuccess(Uri uri) {
                                                                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                                                                    if (currentUser != null){
                                                                                        FirebaseFirestore dbUser = FirebaseFirestore.getInstance();

                                                                                        String userId = Objects.requireNonNull(user).getUid();

                                                                                        FirebaseMessaging.getInstance().getToken()
                                                                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<String> task) {

                                                                                                        if (!task.isSuccessful()){
                                                                                                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                                                                                            return;
                                                                                                        }

                                                                                                        // Get new FCM registration token
                                                                                                        String token = task.getResult();

                                                                                                        User NewUser = new User(getName, getEmail, getPassword, token, userId, uri.toString());

                                                                                                        firestore.collection("UserDetails")
                                                                                                                .document(userId)
                                                                                                                .set(NewUser)
                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(Void unused) {

                                                                                                                        // Data uploaded successfully
                                                                                                                        // Handle success message or further actions
                                                                                                                        Log.d(TAG, "User data uploaded to Firestore for user ID: " + userId);
                                                                                                                        error.setText("Registration Successful, click the Log in button to sign in");
                                                                                                                        Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                                                                                        dialog.dismiss();
                                                                                                                    }
                                                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                                                    @Override
                                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                                        // Handle errors
                                                                                                                        Log.e(TAG, "Error uploading user data for user ID " + userId + ": " + e.getMessage());
                                                                                                                        Toast.makeText(Registration.this, "Failed to register", Toast.LENGTH_SHORT).show();
                                                                                                                        dialog.dismiss();
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Toast.makeText(Registration.this, "Failed to get image url, try again", Toast.LENGTH_SHORT).show();
                                                                                    dialog.dismiss();
                                                                                }
                                                                            });
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(Registration.this, "Failed to get image url, try again", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss();
                                                                        }
                                                                    });

                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Registration.this, "Failed to register, try again", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                });
                                    }
                                } else {
                                    // Failed to check email existence
                                    Exception exception = task.getException();
                                    if (exception != null) {
                                        // Handle exception
                                        Toast.makeText(Registration.this, "Failed to check email, please ensure you have strong  internet", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });


                }else {
                    TextView noImage = findViewById(R.id.noImage);
                    noImage.setText("No image selected, please select image");
                    Toast.makeText(Registration.this, "No image selected, please select image", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        ingia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration.this, LogIn.class);
                startActivity(intent);
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}