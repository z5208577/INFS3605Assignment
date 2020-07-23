package com.example.INFS3605App.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getName();
    public EditText name, email, password, confirmPassword, companyCode;
    public String nameInput, emailInput, passwordInput, confirmPasswordInput, companyCodeInput;
    public int reqCode;
    public Button createUser, generateCode;
    public ImageView userDp, codeInfo;
    public Uri userDpURI;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        reqCode = 1;



        // UI functionalities grouped by order of appearance in register screen
        // userDp functionality:
        userDp = findViewById(R.id.userDp);
        userDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking required permissions.
                if (Build.VERSION.SDK_INT>=22){
                    if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                            Toast.makeText(RegisterActivity.this, "Please accept required permissions", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},reqCode);
                        }
                    } else {

                        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                        gallery.setType("image/*");
                        startActivityForResult(gallery,1);
                    }
                } else {
                    Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery.setType("image/*");
                    startActivityForResult(gallery,1);
                }
            }
        });



        name =findViewById(R.id.name);
        name.addTextChangedListener(registerTextWatcher);


        email = findViewById(R.id.forgottenPassEmail);
        email.addTextChangedListener(registerTextWatcher);
        password = findViewById(R.id.password);
        password.addTextChangedListener(registerTextWatcher);
        confirmPassword = findViewById(R.id.confirmPassword);
        confirmPassword.addTextChangedListener(registerTextWatcher);
        companyCode = findViewById(R.id.companyCode);
        companyCode.addTextChangedListener(registerTextWatcher);
        codeInfo = findViewById(R.id.codeInfo);
        codeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "This code is used to access your relevant discussion forum. " +
                        "Ask for it from your manager or create one yourself and distribute it amongst employees", Toast.LENGTH_LONG).show();

            }
        });


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

        generateCode =findViewById(R.id.generateCode);
        generateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companyCode.setText(UUID.randomUUID().toString());
            }
        });

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
                                updateUserInfo(user);
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
                                User newUser = new User(user.getUid(),companyCodeInput);
                                FirebaseDatabase mFireDatabase =  FirebaseDatabase.getInstance();
                                DatabaseReference myRef = mFireDatabase
                                        .getReference("Users")
                                        .child(user.getUid());
                                myRef.setValue(newUser);
                                Log.d(TAG, "User added");
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                RegisterActivity.this.startActivity(intent);
                                mFirebaseAuth.signOut();
                                Toast.makeText(RegisterActivity.this, "Check your inbox for the verification email", Toast.LENGTH_SHORT).show();

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
    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nameInput = name.getText().toString().trim();
            emailInput = email.getText().toString().trim();
            passwordInput = password.getText().toString().trim();
            confirmPasswordInput = confirmPassword.getText().toString().trim();
            companyCodeInput = companyCode.getText().toString().trim();

            createUser.setEnabled(!nameInput.isEmpty()
                    && !emailInput.isEmpty()
                    && !passwordInput.isEmpty()
                    && !confirmPasswordInput.isEmpty()
                    && !companyCodeInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void updateUserInfo(final FirebaseUser user) {
        StorageReference mStrorage = FirebaseStorage.getInstance().getReference().child("userDps");
        if (userDpURI != null) {
            final StorageReference imageFilePath = mStrorage.child(userDpURI.getLastPathSegment());
            imageFilePath.putFile(userDpURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameInput)
                                    .setPhotoUri(uri)
                                    .build();

                            user.updateProfile(profileUpdate);
                        }
                    });
                }
            });
        } else {
            Uri myUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/infs3605-32bdc.appspot.com/o/userDps%2FdefaultUser.jpg?alt=media&token=d0ae4498-18f3-4195-a07f-e9ee351273e2");
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nameInput)
                    .setPhotoUri(myUri)
                    .build();
            user.updateProfile(profileUpdate);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == reqCode && data != null) {
            userDpURI = data.getData() ;
            userDp.setImageURI(userDpURI);

        }
    }

}