package com.getjobtzben.admin;

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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.getjobtzben.admin.model.Project;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Post extends AppCompatActivity {

    private StorageReference storageReference;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
    private ProgressBar progressBar;

    EditText projTitle;
    EditText projDesc;
    ImageView imageView;
    Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        projTitle = findViewById(R.id.projecTitle);
        projDesc = findViewById(R.id.projecDes);
        imageView = findViewById(R.id.ProjImages);
        progressBar = findViewById(R.id.progressBar);
        upload = findViewById(R.id.upload);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        if (data != null && data.getData() != null) {
                            // Get the selected image URI
                            selectedImageUri = result.getData().getData();

                            Glide.with(Post.this)
                                    .load(selectedImageUri)
                                    .apply(RequestOptions.centerCropTransform())
                                    .into(imageView);

                            imageView.setImageURI(selectedImageUri);

                        }
                    }
                }
        );

        imageView.setOnClickListener(view -> {
            ImagePicker.with(Post.this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickerLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressDialog dialog = new ProgressDialog(Post.this);
                dialog.setMessage("Please wait...");
                dialog.show();

                // Create a Firestore reference
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Create a collection reference for "products" nested inside the businessAccount document
                CollectionReference projRef = db.collection("ProjectTitle");

                if (selectedImageUri != null) {
                    StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImageUri));

                    fileReference.putFile(selectedImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();

                                            String getProjTitle = projTitle.getText().toString().toLowerCase();
                                            String getProjDesc = projDesc.getText().toString().trim();

                                            if (getProjTitle.isEmpty()) {
                                                projTitle.setError("Please, fill Title Name");
                                                projTitle.requestFocus();
                                                dialog.dismiss();
                                                return;
                                            }

                                            if (getProjDesc.isEmpty()) {
                                                projDesc.setError("Please, fill Description Price");
                                                projDesc.requestFocus();
                                                dialog.dismiss();
                                                return;
                                            }

                                            // Assuming 'databaseReference' is a reference to your Firestore collection
                                            Project newProject = new Project(
                                                    uri.toString(),
                                                    getProjTitle
                                            );

                                            String uploadId = db.collection("ProjectTitle").document().getId();
                                            // Add the 'upload' object to Firestore collection
                                            // Add the product to Firestore
                                            projRef.document(uploadId).set(newProject)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Clear input fields or perform UI updates after successful upload
                                                            // Clear input fields
                                                            projTitle.setText("");
                                                            projDesc.setText("");
                                                            // ... (Clear other fields)

                                                            dialog.dismiss();
                                                            Toast.makeText(Post.this, "Upload successful", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            dialog.dismiss();
                                                            Toast.makeText(Post.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });


                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(Post.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                    progressBar.setProgress((int) progress);
                                }
                            });
                } else {
                    dialog.dismiss();
                    Toast.makeText(Post.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}