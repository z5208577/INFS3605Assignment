package com.example.INFS3605App.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.INFS3605App.R;
import com.example.INFS3605App.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getName();
    public EditText name, email, password, confirmPassword;
    public String userID, nameInput, emailInput, passwordInput, confirmPasswordInput;
    public Button createUser;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //populating UI of register activity
        name =findViewById(R.id.name);
        name.addTextChangedListener(loginTextWatcher);


        email = findViewById(R.id.email);
        email.addTextChangedListener(loginTextWatcher);
        password = findViewById(R.id.password);
        password.addTextChangedListener(loginTextWatcher);
        confirmPassword = findViewById(R.id.confirmPassword);
        confirmPassword.addTextChangedListener(loginTextWatcher);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Toast.makeText(RegisterActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainFragmentContainerActivity.class);
                    RegisterActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Please log in", Toast.LENGTH_SHORT).show();
                }

            }
        };

        //firebase implementation
        createUser = findViewById(R.id.createUser);
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmPasswordInput.equals(passwordInput) == false){
                    Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                } else {
                        mFirebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                try
                                {
                                    throw task.getException();
                                }
                                // if user enters wrong email.
                                catch (FirebaseAuthWeakPasswordException weakPassword)
                                {
                                    Log.d(TAG, "onComplete: weak_password");
                                    Toast.makeText(RegisterActivity.this, "Password too weak, try again", Toast.LENGTH_SHORT).show();
                                }
                                // if user enters wrong password.
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                {
                                    Log.d(TAG, "onComplete: malformed_email");
                                    Toast.makeText(RegisterActivity.this, "Invalid email address, try again", Toast.LENGTH_SHORT).show();
                                }
                                catch (FirebaseAuthUserCollisionException existEmail)
                                {
                                    Log.d(TAG, "onComplete: exist_email");
                                    Toast.makeText(RegisterActivity.this, "Email already exists, try the forgotten password link", Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(RegisterActivity.this, "An error has occured, please try again", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onComplete: " + e.getMessage());
                                }
                            } else {
                                //creating a new row for user entity in firebasefirestore
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Email not sent " + e.getMessage());                                    Toast.makeText(RegisterActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(RegisterActivity.this, "Verification email was not sent, try again later", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                User newUser = new User(nameInput);
                                mDatabaseReference.child(user.getUid()).setValue(newUser);
                                Log.d(TAG, "User added");
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                RegisterActivity.this.startActivity(intent);
                                mFirebaseAuth.signOut();
                            }
                        }
                    });
                }
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // preventing accidental login with new accounts
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null && mFirebaseUser.isEmailVerified()) {
                    Toast.makeText(RegisterActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainFragmentContainerActivity.class);
                    RegisterActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Please log in", Toast.LENGTH_SHORT).show();
                }

            }
        };

    }
    //prevents users from logging in without filling in textfields
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nameInput = name.getText().toString().trim();
            emailInput = email.getText().toString().trim();
            passwordInput = password.getText().toString().trim();
            confirmPasswordInput = confirmPassword.getText().toString().trim();

            createUser.setEnabled(!nameInput.isEmpty() && !emailInput.isEmpty() && !passwordInput.isEmpty() && !confirmPasswordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}