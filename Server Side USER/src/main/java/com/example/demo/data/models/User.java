package com.example.demo.data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// This class is for interaction with database and internal use by server. Not accessible directly by frontend
@Entity
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;
    
    private String password;

    private Long sessionId;

    private String userInfo; 

    public User() {}

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Long getSessionId() {
        return this.sessionId;
    }
    public String getUserInfo() {
        return this.userInfo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
