package com.getjobtzben.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    private TextView errorLogIn;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        EditText email = findViewById(R.id.emailLogIn);
        EditText password = findViewById(R.id.passwordLogIn);
        errorLogIn = findViewById(R.id.errorLogin);
        Button logIn = findViewById(R.id.ingiaLogIn);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        logIn.setOnClickListener(view -> {
            String email1 = email.getText().toString().trim();
            String password1 = password.getText().toString().trim();

            if (TextUtils.isEmpty(email1) || TextUtils.isEmpty(password1)) {
                Toast.makeText(SignIn.this, "fill all fields!", Toast.LENGTH_LONG).show();
            }
            if (email1.isEmpty()) {
                email.setError("Please enter email");
                email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
                email.setError("Invalid email!");
                email.requestFocus();
                return;
            }

            if (password1.isEmpty()) {
                password.setError("Enter password");
                password.requestFocus();
                return;
            }

            ProgressDialog dialog = new ProgressDialog(SignIn.this);
            dialog.setMessage("Please wait...");
            dialog.show();

            mAuth.signInWithEmailAndPassword(email1, password1)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if (authResult != null && authResult.getUser() != null) {
                                // User has valid credentials, start MainScreen activity
                                startActivity(new Intent(SignIn.this, Post.class));
                                finish();
                            } else {
                                // User credentials are invalid, show error message
                                dialog.dismiss();
                                Toast.makeText(SignIn.this, "Invalid credentials", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // An error occurred while trying to sign in, show error message
                            dialog.dismiss();
                            Toast.makeText(SignIn.this, "fail to log in!" + e, Toast.LENGTH_LONG).show();
                        }
                    });

        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Intent intent = new Intent(SignIn.this, Post.class);
            startActivity(intent);
            finish();
        }
    }

}