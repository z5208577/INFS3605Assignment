package com.example.INFS3605App.utils;

import com.google.firebase.database.ServerValue;

public class Comment {
    private String content,userId,UserDp, username;
    private Object timestamp;

    public Comment(){

    }
    public Comment(String content, String userId, String userDp, String username) {
        this.content = content;
        this.userId = userId;
        UserDp = userDp;
        this.username = username;
        this.timestamp = ServerValue.TIMESTAMP;;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserDp() {
        return UserDp;
    }

    public void setUserDp(String userDp) {
        UserDp = userDp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
