package com.example.INFS3605App.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.INFS3605App.R;
import com.example.INFS3605App.adapters.CommentAdapter;
import com.example.INFS3605App.utils.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailFragment extends Fragment {
    public ImageView postDetailImage, commentUserDp,postDetailUserDp, likePost;
    public TextView postDetailTitle, postDetailDetails, postDetailContent,postLikes;
    public EditText commentContent;
    public String postId,commentContentInput;
    public Button addComment;
    public RecyclerView commentRecyclerView;
    public CommentAdapter mCommentAdapter;
    public List<Comment> mComment;
    public FirebaseAuth mFirebaseAuth;
    public FirebaseUser mFirebaseUser;
    public FirebaseDatabase mFirebaseDatabase;


    public PostDetailFragment() {
    }

    public static PostDetailFragment newInstance(String param1, String param2) {
        PostDetailFragment fragment = new PostDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        postDetailImage = view.findViewById(R.id.postDetailImage);
        if(!getArguments().getString("postImage").equals("defaultPostImage")){
            Glide.with(this).load(getArguments().getString("postImage")).into(postDetailImage);
        }
        postDetailTitle = view.findViewById(R.id.postDetailTitle);
        postDetailTitle.setText(getArguments().getString("postTitle"));

        postDetailContent = view.findViewById(R.id.postDetailContent);
        postDetailContent.setText(getArguments().getString("postContent"));

        postDetailDetails = view.findViewById(R.id.postDetailDetails);
        String date = timestampToString(getArguments().getLong("postDate"))
                + " | By " +getArguments().getString("postUser");
        postDetailDetails.setText(date);

        postDetailUserDp = view.findViewById(R.id.postDetailUserDp);
        Glide.with(this).load(getArguments().getString("postUserDp"))
                .apply(RequestOptions.circleCropTransform()).into(postDetailUserDp);

        postId= getArguments().getString("postId");

        postLikes = view.findViewById(R.id.postLikes);
        postLikes.setText(Integer.toString(getArguments().getInt("postLikes")));

        likePost = view.findViewById(R.id.likePost);
        likePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference likesRefrence = mFirebaseDatabase.getReference("Posts")
                        .child(postId)
                        .child("likes");
                likesRefrence.setValue((getArguments().getInt("postLikes"))+1);
                likesRefrence.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postLikes.setText(snapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(),"Likes display did not change.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        commentContent = view.findViewById(R.id.commentContent);
        commentContent.addTextChangedListener(commentTextWatcher);
        commentUserDp = view.findViewById(R.id.commentUserDp);
        if (mFirebaseUser.getPhotoUrl()!=null) {
            Glide.with(this).load(mFirebaseUser.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform()).into((commentUserDp));
         } else {
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/infs3605-32bdc.appspot.com/o/userDps%2FdefaultUser.jpg?alt=media&token=d0ae4498-18f3-4195-a07f-e9ee351273e2" )
                    .apply(RequestOptions.circleCropTransform()).into(commentUserDp);
        }

        addComment = view.findViewById(R.id.addComent);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //similar to creating a post object in forumFragment
                addComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = mFirebaseDatabase.getReference("Comment")
                        .child(postId)
                        .push();
                String commentInput = commentContent.getText().toString();
                String userId = mFirebaseUser.getUid();
                String userDp;
                if (mFirebaseUser.getPhotoUrl() != null){
                    userDp = mFirebaseUser.getPhotoUrl().toString();
                } else {
                    userDp= "https://firebasestorage.googleapis.com/v0/b/infs3605-32bdc.appspot.com/o/userDps%2FdefaultUser.jpg?alt=media&token=d0ae4498-18f3-4195-a07f-e9ee351273e2";
                }
                String username = mFirebaseUser.getDisplayName();
                Comment comment = new Comment(commentInput,userId,userDp,username);
                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),"Comment added.", Toast.LENGTH_SHORT).show();
                        addComment.setVisibility(View.VISIBLE);
                        commentContent.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Comment was not added.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //displaying comments
        commentRecyclerView = view.findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DatabaseReference commentRef = mFirebaseDatabase.getReference("Comment").child(postId);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mComment = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    mComment.add(comment);
                }

                mCommentAdapter = new CommentAdapter(getContext(),mComment);
                commentRecyclerView.setAdapter(mCommentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"New Comment was not displayed", Toast.LENGTH_SHORT).show();
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

    private TextWatcher commentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           commentContentInput = commentContent.getText().toString().trim();
            addComment.setEnabled(!commentContentInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
