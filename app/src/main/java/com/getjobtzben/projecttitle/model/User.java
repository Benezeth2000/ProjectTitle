package com.getjobtzben.projecttitle.model;

public class User {

    private String Jina, Email, Password, Token, userUid, userProfile;

    public User() {
    }

    public User(String jina, String email, String password,  String token, String userUid, String userProfile) {
        Jina = jina;
        Email = email;
        Password = password;
        Token = token;
        this.userUid = userUid;
        this.userProfile = userProfile;
    }

    public String getJina() {
        return Jina;
    }

    public void setJina(String jina) {
        Jina = jina;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

}
