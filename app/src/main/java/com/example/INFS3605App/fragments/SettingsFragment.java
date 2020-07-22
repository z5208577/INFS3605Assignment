package com.example.INFS3605App.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.INFS3605App.R;
import com.example.INFS3605App.adapters.PostAdapter;
import com.example.INFS3605App.ui.ForgottenPasswordActivity;
import com.example.INFS3605App.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.CLIPBOARD_SERVICE;

public class SettingsFragment extends Fragment {
    public TextView settingsCompanyCode, settingsUsername, settingsEmail,settingsJoinDate,settingsChangePass;
    public ImageView settingsUserDp,copyCompId;
    public FirebaseUser currentUser;
    public FirebaseAuth mFirebaseAuth;
    private Uri userDpUri = null;
    public StorageReference imageFilePath;
    public FirebaseDatabase mFireDatabase;
    public DatabaseReference mUserDatabaseReference;
    public SettingsFragment() {
        // Required empty public constructor
    }
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        mFireDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFireDatabase.getReference("Users");


        settingsCompanyCode = view.findViewById(R.id.settingsCompanyCode);

        settingsUsername = view.findViewById(R.id.settingsUsername);
        settingsUsername.setText(currentUser.getDisplayName());

        settingsEmail = view.findViewById(R.id.settingsEmail);
        settingsEmail.setText(currentUser.getEmail());

        settingsJoinDate = view.findViewById(R.id.settingsJoinDate);
        mUserDatabaseReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUser.getUid());
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                settingsCompanyCode.setText(user.getCompanyId());
                settingsJoinDate.setText(timestampToString((long)user.getJoinDate()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        copyCompId = view.findViewById(R.id.copyCompId);
        copyCompId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager myClipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                String text;
                text = settingsCompanyCode.getText().toString();

                ClipData myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getContext(), "Company Code copied. Share with your co-workers",Toast.LENGTH_SHORT).show();
            }
        });

        settingsChangePass = view.findViewById(R.id.settingsChangePass);
        SpannableString changePass = new SpannableString("Change my Password");
        changePass.setSpan(new UnderlineSpan(), 0, changePass.length(), 0);
        settingsChangePass.setText(changePass);
        settingsChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(currentUser.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Password reset link sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Given email address was not valid or not yet used", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        settingsUserDp = view.findViewById(R.id.settingsUserDp);
        if (currentUser.getPhotoUrl()!=null){
            Glide.with(this).load(currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(settingsUserDp);
        } else {
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/infs3605-32bdc.appspot.com/o/userDps%2FdefaultUser.jpg?alt=media&token=d0ae4498-18f3-4195-a07f-e9ee351273e2 " )
                    .apply(RequestOptions.circleCropTransform()).into(settingsUserDp);
        }
        settingsUserDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=22){
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                            Toast.makeText(getContext(), "Please accept required permissions", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
                        }
                    } else {

                        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                        gallery.setType("image/*");
                        startActivityForResult(gallery,3);
                    }
                } else {
                    Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery.setType("image/*");
                    startActivityForResult(gallery,3);
                }

            }
        });
        return view;
    }
    public String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm dd-MM-yyyy",calendar).toString();
        return date;
    }

    private void updateUserInfo(final FirebaseUser user) {
        StorageReference mStrorage = FirebaseStorage.getInstance().getReference().child("userDps");
        if (userDpUri != null) {
            final StorageReference imageFilePath = mStrorage.child(userDpUri.getLastPathSegment());
            imageFilePath.putFile(userDpUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
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
                    .setPhotoUri(myUri)
                    .build();
            user.updateProfile(profileUpdate);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 3 && data != null) {
            userDpUri = data.getData() ;
            settingsUserDp.setImageURI(userDpUri);
            updateUserInfo(currentUser);
            if (currentUser.getPhotoUrl()!=null){
                Glide.with(this).load(currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(settingsUserDp);
            } else {
                Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/infs3605-32bdc.appspot.com/o/userDps%2FdefaultUser.jpg?alt=media&token=d0ae4498-18f3-4195-a07f-e9ee351273e2 " )
                        .apply(RequestOptions.circleCropTransform()).into(settingsUserDp);
            }
            Toast.makeText(getContext(), "Your profile picture will be updated when you log back in.", Toast.LENGTH_SHORT).show();

        }
    }

}
