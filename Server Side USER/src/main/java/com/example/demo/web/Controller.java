package com.example.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.data.models.User;
import com.example.demo.data.payloads.UserAuth;
import com.example.demo.service.AuthServiceImpl;

@RestController
public class Controller {
    
    @Autowired
    AuthServiceImpl authServiceImpl;

    @PatchMapping("/auth") // should be GET but needs body
    public ResponseEntity<?> checkUser(@RequestBody UserAuth userAuth)
    {
        String sessionId = authServiceImpl.checkValidityOfPassword(userAuth);
        if(sessionId!=null)
        {
            HttpCookie cookie1 = ResponseCookie.from("sessionId", sessionId).maxAge(3600).path("/secure").secure(true).httpOnly(true).build();
            HttpCookie cookie2 = ResponseCookie.from("username", userAuth.getUsername()).maxAge(3600).path("/secure").secure(true).httpOnly(true).build();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, cookie1.toString());
            headers.add(HttpHeaders.SET_COOKIE, cookie2.toString());
            return new ResponseEntity<String>(sessionId, headers, HttpStatus.OK);
        }
        else
            return new ResponseEntity<String>("Incorrect Password", HttpStatus.UNAUTHORIZED);
    }  

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody UserAuth userAuth)
    {
        String response = authServiceImpl.createUser(userAuth);
        if(response=="ALREADY_EXISTS")
            return new ResponseEntity<String>(response, HttpStatus.OK);
        else 
        {
            HttpCookie cookie1 = ResponseCookie.from("sessionId", response).maxAge(3600).path("/secure").secure(true).httpOnly(true).build();
            HttpCookie cookie2 = ResponseCookie.from("username", userAuth.getUsername()).maxAge(3600).path("/secure").secure(true).httpOnly(true).build();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, cookie1.toString());
            headers.add(HttpHeaders.SET_COOKIE, cookie2.toString());
            return new ResponseEntity<String>(headers, HttpStatus.OK);
        }
    }

    @PutMapping("/secure/auth")
    public ResponseEntity<?> clearCookies()
    {
        HttpCookie cookie1 = ResponseCookie.from("sessionId", null).maxAge(0).path("/secure").secure(true).httpOnly(true).build();
        HttpCookie cookie2 = ResponseCookie.from("username", null).maxAge(0).path("/secure").secure(true).httpOnly(true).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie1.toString());
        headers.add(HttpHeaders.SET_COOKIE, cookie2.toString());
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @DeleteMapping("/secure/deleteAccount")
    public ResponseEntity<?> deleteUser(@CookieValue("username") String username)
    {
        authServiceImpl.deleteUser(username);
        HttpCookie cookie1 = ResponseCookie.from("sessionId", null).maxAge(0).path("/secure").secure(true).httpOnly(true).build();
        HttpCookie cookie2 = ResponseCookie.from("username", null).maxAge(0).path("/secure").secure(true).httpOnly(true).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie1.toString());
        headers.add(HttpHeaders.SET_COOKIE, cookie2.toString());
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @PutMapping("/secure/updateAccount/username")
    public ResponseEntity<?> updateUsername(@CookieValue("username") String username, @RequestBody String newUsername)
    {
        String result = authServiceImpl.updateUsername(username, newUsername);
        if(result=="ALREADY_EXISTS")
            return new ResponseEntity<String>(result, HttpStatus.OK);
        
        HttpCookie cookie2 = ResponseCookie.from("username", newUsername).maxAge(3600).path("/secure").secure(true).httpOnly(true).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie2.toString());
        return new ResponseEntity<String>(result, headers, HttpStatus.OK);
    }

    @PutMapping("/secure/updateAccount/password")
    public ResponseEntity<?> updatePassword(@CookieValue("username") String username, @RequestBody UserAuth userAuth)
    {
        boolean result = authServiceImpl.updatePassword(username, userAuth);
        if(result)
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<String>("Incorrect Password", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/secure/info")
    public ResponseEntity<User> getUser(@CookieValue("username") String username)
    {
        return new ResponseEntity<User>(authServiceImpl.getUserInfo(username), HttpStatus.OK);
    }

    @PutMapping("/secure/info")
    public ResponseEntity<?> setUser(@CookieValue("username") String username, @RequestBody String userInfo)
    {
        authServiceImpl.setUserInfo(username, userInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
