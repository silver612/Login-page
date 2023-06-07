package com.example.demo.service;

import org.springframework.stereotype.Component;

import com.example.demo.data.models.User;
import com.example.demo.data.payloads.UserAuth;

@Component
public interface AuthService{

    public String checkValidityOfPassword(UserAuth userAuth);
    public boolean checkSessionId(String username, String sessionId);
    public String createUser(UserAuth userAuth);
    public void deleteUser(String username);
    public String updateUsername(String username, String newUsername);
    public boolean updatePassword(String username, UserAuth userAuth);
    public User getUserInfo(String username);
    public void setUserInfo(String username, String userInfo);

}
