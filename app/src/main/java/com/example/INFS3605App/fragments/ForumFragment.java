package com.example.INFS3605App.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.INFS3605App.R;
import com.example.INFS3605App.ui.RegisterActivity;
import com.example.INFS3605App.utils.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment {
    Dialog popupCreatePost;
    public ImageView newPostUserDp, newPostPostImage, createPost;
    public TextView newPostTitle, newPostContent;
    public FirebaseUser currentUser;
    public FirebaseAuth mFirebaseAuth;
    private Uri postImageUri = null;
    public ProgressBar postPorgressBar;
    public StorageReference imageFilePath;

    public ForumFragment() {
        // Required empty public constructor
    }

    public static ForumFragment newInstance(String param1, String param2) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_company_discussion_board, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
       popupCreatePost = new Dialog(this.getContext());
       initiatePopup();
       FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.createPost);
       fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               popupCreatePost.show();
           }
       });
        return view;
    }

    public void initiatePopup(){

        popupCreatePost.setContentView(R.layout.post_popup);
        popupCreatePost.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        popupCreatePost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popupCreatePost.getWindow().getAttributes().gravity= Gravity.TOP;


        newPostUserDp= popupCreatePost.findViewById(R.id.newpost_userDp);
        postPorgressBar = popupCreatePost.findViewById(R.id.postProgressBar);
        //method to create a post
        newPostPostImage = popupCreatePost.findViewById(R.id.newpost_image);
        newPostPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // method to allow user to select an image to post. similar to register select DP
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
                        startActivityForResult(gallery,2);
                    }
                } else {
                    Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery.setType("image/*");
                    startActivityForResult(gallery,2);
                }
            }
        });
        newPostTitle = popupCreatePost.findViewById(R.id.newpost_title);
        newPostContent = popupCreatePost.findViewById(R.id.newpost_content);


        createPost = popupCreatePost.findViewById(R.id.newpost_post);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if inputs are fufilled
                if (!newPostTitle.getText().toString().isEmpty() && !newPostContent.getText().toString().isEmpty()){

                    //checks if post is too large
                    if(newPostTitle.getText().toString().length() > 50 && newPostContent.getText().toString().length() > 200){
                        Toast.makeText(getContext(), "Post content or title too large.", Toast.LENGTH_SHORT).show();
                    //else send post object to database
                    } else {
                        postPorgressBar.setVisibility(View.VISIBLE);
                        createPost.setVisibility(View.INVISIBLE);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("forum_images");
                        // checks if no image was attached to post
                        if(postImageUri == null){
                            makePost("defaultPostImage");
                        } else {
                            //post with image
                            imageFilePath = storageReference.child(postImageUri.getLastPathSegment());
                            imageFilePath.putFile(postImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //creating post object with image
                                            String imageDownloadLink = uri.toString();
                                            makePost(imageDownloadLink);
                                        }
                                        //failed imageupload
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Please try again.", Toast.LENGTH_SHORT).show();
                                            createPost.setVisibility(View.VISIBLE);
                                            postPorgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Invalid Post details.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (currentUser.getPhotoUrl()!=null){
            Glide.with(this).load(currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(newPostUserDp);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2 && data != null) {
            postImageUri = data.getData() ;
            newPostPostImage.setImageURI(postImageUri);

        }
    }
    public void makePost(String image){
        Post post = new Post(newPostTitle.getText().toString()
                ,newPostContent.getText().toString()
                ,image
                ,currentUser.getDisplayName()
                ,currentUser.getUid()
                ,currentUser.getPhotoUrl().toString());

        //post object is added into firebase
        FirebaseDatabase mFireDatabase =  FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFireDatabase.getReference("Posts").push();
        String key = myRef.getKey();
        post.setPostId(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                createPost.setVisibility(View.VISIBLE);
                postPorgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Post was posted.", Toast.LENGTH_SHORT).show();
                popupCreatePost.dismiss();
            }
        });
    }
}
