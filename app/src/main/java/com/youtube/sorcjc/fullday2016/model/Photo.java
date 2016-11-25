package com.youtube.sorcjc.fullday2016.model;

import com.google.firebase.database.Exclude;

public class Photo {

    private String name;
    private int userId;

    @Exclude
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
