package com.example.INFS3605App.utils;

import com.google.firebase.database.ServerValue;

public class User {
    private String userId, companyId;
    private Object joinDate;

    public User(String userId, String companyId) {
        this.userId = userId;
        this.companyId = companyId;
        this.joinDate = ServerValue.TIMESTAMP;
    }

    public User(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Object getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Object joinDate) {
        this.joinDate = joinDate;
    }
}
