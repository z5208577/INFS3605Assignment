package com.example.INFS3605App.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseUtils {
    public static DatabaseReference getUserRef(String email){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.USER_KEY)
                .child(email);
    }

    public static DatabaseReference getPostRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.POST_KEY);
    }

    public static DatabaseReference getPostLikedRef(){
        return FirebaseDatabase.getInstance()
                .getReference(Constants.POST_LIKED_KEY);
    }

    public static DatabaseReference getPostLikedRef(String postID){
        return getPostRef().child(getCurrentUser().getEmail()
                .replace(".",","))
                .child(postID);
    }

    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUid(){
        String path = FirebaseDatabase.getInstance().getReference().push().toString();
        return path.substring(path.lastIndexOf("/")+1);
    }

    public static StorageReference getImageSRef(){
        return FirebaseStorage.getInstance().getReference(Constants.POST_IMAGES);
    }

    public static DatabaseReference getMyPostRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.MY_POST)
                .child(getCurrentUser().getEmail().replace(".",","));
    }

    public static DatabaseReference getCommentRef(String postID){
        return FirebaseDatabase.getInstance().getReference(Constants.COMMENTS_KEY)
                .child(postID);
    }

    public static DatabaseReference getMyRecordRef(){
        return FirebaseDatabase.getInstance().getReference(Constants.USER_RECORD)
                .child(getCurrentUser().getEmail().replace(".",","));
    }

    public static void addToMyRecord(String node, final String id){
        getMyRecordRef().child(node).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                ArrayList<String> myRecordCollection;
                if(currentData.getValue()== null){
                    myRecordCollection = new ArrayList<String>(1);
                    myRecordCollection.add(id);
                } else {
                    myRecordCollection = (ArrayList<String>) currentData.getValue();
                    myRecordCollection.add(id);
                }
                currentData.setValue(myRecordCollection);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }
}
