package com.getjobtzben.projecttitle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getjobtzben.projecttitle.adapter.MainScreenAdapter;
import com.getjobtzben.projecttitle.model.Project;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainScreenAdapter mainScreenAdapter;
    private List<Project> mProject;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        recyclerView = findViewById(R.id.recyclerMainScreen);
        progressBar = findViewById(R.id.progress_circle1);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mProject = new ArrayList<>();

        retrieveAll();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }

    private void retrieveAll() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("ProjectTitle")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Project> mUpload = new ArrayList<>(); // Create a list for products

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Project project = document.toObject(Project.class);
                            mUpload.add(project);
                        }

                        Collections.reverse(mUpload);
                        mainScreenAdapter = new MainScreenAdapter(MainScreen.this, mProject);
                        recyclerView.setAdapter(mainScreenAdapter);
                        mainScreenAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.VISIBLE);
                        int progressBarDuration = 10000; // Set the duration in milliseconds (e.g., 10 seconds)
                        new Handler().postDelayed(() -> {
                            progressBar.setVisibility(View.GONE);
                        }, progressBarDuration);

                    })
                    .addOnFailureListener(e -> {
                        Log.d("Firestore", "Error getting documents: ", e);
                    });
        }
    }

}