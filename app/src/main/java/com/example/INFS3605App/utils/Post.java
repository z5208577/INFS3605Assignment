package com.example.INFS3605App.utils;

import com.google.firebase.database.ServerValue;

public class Post {
    private String PostId, title, content,image,user,userId,userDp, company;
    private int likes = 0;
    private Object timeStamp;

    public Post(String title, String content, String image, String user, String userId, String userDp) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.user = user;
        this.userId = userId;
        this.userDp = userDp;
        this.timeStamp = ServerValue.TIMESTAMP;
        company = "Test Company";
    }

    public Post(String title, String content, String image, String user, String userId, String userDp, String company) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.user = user;
        this.userId = userId;
        this.userDp = userDp;
        this.company = company;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Post(){

    }


    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserDp() {
        return userDp;
    }

    public void setUserDp(String userDp) {
        this.userDp = userDp;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
