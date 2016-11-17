package com.youtube.sorcjc.fullday2016.io.response;

public class LoginResponse {

    private String token;
    private boolean error;

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
}
