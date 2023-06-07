package com.example.demo.data.payloads;

import org.springframework.lang.NonNull;

// This class is for interaction with frontend only
public class UserAuth {

    @NonNull
    private String username;

    @NonNull
    private String password;

    private String newUsername;

    private String newPassword;

    public UserAuth(){
        this.username = "";
        this.password = "";
        this.newUsername = "";
        this.newPassword = "";
    }

    public UserAuth(@NonNull String username, @NonNull String password){
        this.username = username;
        this.password = password;
        this.newUsername = "";
        this.newPassword = "";
    }

    public UserAuth(@NonNull String username, @NonNull String password, String newUsername, String newPassword){
        this.username = username;
        this.password = password;
        this.newUsername = newUsername;
        this.newPassword = newPassword;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setUsername(@NonNull String username){
        this.username = username;
    }

    public void setPassword(@NonNull String password){
        this.password = password;
    }

    public String getNewUsername(){
        return newUsername;
    }

    public String getNewPassword(){
        return newPassword;
    }

    public void setNewUsername(String username){
        this.newUsername = username;
    }

    public void setNewPassword(String password){
        this.newPassword = password;
    }
}
