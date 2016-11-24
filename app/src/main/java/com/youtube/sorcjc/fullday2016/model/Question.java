package com.youtube.sorcjc.fullday2016.model;

import com.google.firebase.database.Exclude;

public class Question {

    private String description;
    private int likes;
    private String user;

    @Exclude
    private String key;
    @Exclude
    private boolean liked;

    public Question() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
