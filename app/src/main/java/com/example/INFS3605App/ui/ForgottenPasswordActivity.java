package com.example.INFS3605App.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.INFS3605App.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgottenPasswordActivity extends AppCompatActivity {
    public EditText forgottenPassEmail;
    public Button sendResetEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);
        forgottenPassEmail = findViewById(R.id.forgottenPassEmail);
        sendResetEmail = findViewById(R.id.sendResetEmail);
        sendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forgottenPassEmail.getText().toString().trim() != null) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(forgottenPassEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgottenPasswordActivity.this, "Password reset link sent", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ForgottenPasswordActivity.this, "Given email address was not valid or not yet used", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ForgottenPasswordActivity.this, "Enter your email in the relevant field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
