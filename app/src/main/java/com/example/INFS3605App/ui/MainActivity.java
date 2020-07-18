package com.example.INFS3605App.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.INFS3605App.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    public EditText email, password;
    public String emailInput,passwordInput;
    public Button signIn, resendCode, skipSignIn;
    public TextView register, forgotPassword;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUserMetadata metadata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //testing purposes will remove whe put in production
        skipSignIn = findViewById(R.id.skipSignIn);
        skipSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signInWithEmailAndPassword("stevenchung23@gmail.com","123456").addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Incorrect credentials",Toast.LENGTH_SHORT).show();
                        } else {
                            if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                Intent intent = new Intent(MainActivity.this, MainFragmentContainerActivity.class);
                                MainActivity.this.startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this,"Email is not verified, check email inbox or press to resend email",Toast.LENGTH_SHORT).show();
                                resendCode.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });
            }
        });


        mFirebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        email.addTextChangedListener(loginTextWatcher);
        password = findViewById(R.id.password);
        password.addTextChangedListener(loginTextWatcher);

        register = findViewById(R.id.register);
        SpannableString content = new SpannableString("New here? Press to Register");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        register.setText(content);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        //preventing accidental logins
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser !=null){
                    Toast.makeText(MainActivity.this,"Logged in",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainFragmentContainerActivity.class);
                    MainActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this,"Please log in",Toast.LENGTH_SHORT).show();
                }

            }
        };

        // firebase implementation of authentication
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signInWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Incorrect credentials or no connection to internet",Toast.LENGTH_SHORT).show();
                        } else {
                            if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                Intent intent = new Intent(MainActivity.this, MainFragmentContainerActivity.class);
                                MainActivity.this.startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this,"Email is not verified, check email inbox or press to resend email",Toast.LENGTH_SHORT).show();
                                resendCode.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });
            }
        });

        //Resending Verification Email,
        resendCode = findViewById(R.id.resendCode);
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        Toast.makeText(MainActivity.this, "Verification email was not sent, try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        // method for password recovery, mostly done through firebase
        forgotPassword = findViewById(R.id.forgotPassword);
        SpannableString forgotPass = new SpannableString("Forgotten your password? Reset it here.");
        forgotPass.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotPassword.setText(forgotPass);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailInput != null){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailInput)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(MainActivity.this, "Password reset link sent", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Given email address was not valid or not yet used", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    //checks if text fields are empty. If both are not, then the sign-in button is enabled.
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            emailInput = email.getText().toString().trim();
            passwordInput = password.getText().toString().trim();

            signIn.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
