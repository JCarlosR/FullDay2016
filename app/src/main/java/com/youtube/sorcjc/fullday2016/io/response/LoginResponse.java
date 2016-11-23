package com.youtube.sorcjc.fullday2016.io.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    private String token;

    private boolean error;

    private String name;

    @SerializedName("user_id")
    private int userId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

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
}
