package com.example.together.model;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String user_id;
    private String username;
    private String email;
    private String profile_img_url;

    public User() {
    }

    public User(String user_id, String username, String email, String profile_img_url) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.profile_img_url = profile_img_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_img_url() {
        return profile_img_url;
    }

    public void setProfile_img_url(String profile_img_url) {
        this.profile_img_url = profile_img_url;
    }
}
