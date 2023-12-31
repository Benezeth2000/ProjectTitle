package com.getjobtzben.projecttitle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.getjobtzben.projecttitle.adapter.MainScreenAdapter;
import com.getjobtzben.projecttitle.model.Project;
import com.getjobtzben.projecttitle.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MainScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainScreenAdapter mainScreenAdapter;
    private List<Project> mProject;
    private ProgressBar progressBar;
    private TextView userName;
    private ImageView UserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        recyclerView = findViewById(R.id.recyclerMainScreen);
        progressBar = findViewById(R.id.progress_circle1);
        userName = findViewById(R.id.username);
        UserPhoto = findViewById(R.id.image);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mProject = new ArrayList<>();

        retrieveAll();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("UserDetails").document(currentUserId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle errors
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            // Data exists for the current user
                            User currentUser = snapshot.toObject(User.class);
                            if (currentUser != null) {
                                userName.setText(currentUser.getJina());

                                Glide.with(MainScreen.this)
                                        .load(currentUser.getUserProfile())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(UserPhoto);
                            }
                        } else {
                            // Document doesn't exist
                        }
                    }
                });
    }

    private void retrieveAll() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("ProjectTitle")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                mProject.clear();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Project upload = document.toObject(Project.class);
                                    mProject.add(upload);
                                }

                                mainScreenAdapter = new MainScreenAdapter(MainScreen.this, mProject);
                                recyclerView.setAdapter(mainScreenAdapter);
                                mainScreenAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.INVISIBLE);
                            } else {
                                Toast.makeText(MainScreen.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }
    }

}