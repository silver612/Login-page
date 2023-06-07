package com.example.demo.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.data.models.User;
import com.example.demo.data.payloads.UserAuth;
import com.example.demo.data.repository.UserRepository;
import com.example.demo.exception.ResourceNotFoundException;

@Service
public class AuthServiceImpl implements AuthService{
    
    @Autowired
    UserRepository userRepository;

    private String encryptPassword(String password)
    {
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInteger = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(bigInteger.toString(16));
            while(hexString.length() < 32)
                hexString.insert(0, '0');
            return hexString.toString();
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm");
            return null;
        }
    }

    @Override
    public String checkValidityOfPassword(UserAuth userAuth)
    {
        Optional<User> userMaybe = userRepository.findByName(userAuth.getUsername());
        if(userMaybe.isEmpty())
            throw new ResourceNotFoundException(userAuth.getUsername());
        User user = userMaybe.get();
        if(encryptPassword(userAuth.getPassword()).equals(user.getPassword()))
        {
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.setSeed(System.currentTimeMillis());
            Long sessionId = secureRandom.nextLong();
            user.setSessionId(sessionId);
            userRepository.save(user);

            return sessionId.toString();
        }
        else
            return null;
    }

    @Override 
    public boolean checkSessionId(String username, String sessionId)
    {
        Optional<User> userMaybe = userRepository.findByName(username);
        if(userMaybe.isEmpty())
            throw new ResourceNotFoundException(username);
        User user = userMaybe.get();
        if(user.getSessionId().toString().equals(sessionId))
            return true;
        else
            return false;
    }

    @Override
    public String createUser(UserAuth userAuth)
    {
        if(userRepository.findByName(userAuth.getUsername()).isPresent())
            return "ALREADY_EXISTS";

        User user = new User();
        user.setUsername(userAuth.getUsername());
        user.setPassword(encryptPassword(userAuth.getPassword()));

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(System.currentTimeMillis());
        Long sessionId = secureRandom.nextLong();
        user.setSessionId(sessionId);
        
        userRepository.save(user);

        return sessionId.toString();
    }

    @Override
    public void deleteUser(String username)
    {
        Optional<User> userMaybe = userRepository.findByName(username);
        if(userMaybe.isEmpty())
            throw new ResourceNotFoundException(username);

        userRepository.deleteById(userMaybe.get().getId());
    }

    @Override
    public String updateUsername(String username, String newUsername)
    {
        Optional<User> userMaybe = userRepository.findByName(username);
        if(userMaybe.isEmpty())
            throw new ResourceNotFoundException(username);

        if(userRepository.findByName(newUsername).isPresent())
            return "ALREADY_EXISTS";

        User user = userMaybe.get();
        user.setUsername(newUsername);
        userRepository.save(user);
        return "OK";
    }

    @Override
    public boolean updatePassword(String username, UserAuth userAuth)
    {
        Optional<User> userMaybe = userRepository.findByName(username);
        if(userMaybe.isEmpty())
            throw new ResourceNotFoundException(username);
        User user = userMaybe.get();
        if(encryptPassword(user.getPassword()).equals(userAuth.getPassword())){
            user.setPassword(userAuth.getNewPassword());
            userRepository.save(user);
            return true;
        }
        else
            return false;
    }

    @Override
    public User getUserInfo(String username)
    {
        Optional<User> userMaybe = userRepository.findByName(username);
        if(userMaybe.isEmpty())
            throw new ResourceNotFoundException(username);
        return userMaybe.get();
    }

    @Override 
    public void setUserInfo(String username, String newUserInfo)
    {
        Optional<User> userMaybe = userRepository.findByName(username);
        if(userMaybe.isEmpty())
            throw new ResourceNotFoundException(username);
        User user = userMaybe.get();
        user.setUserInfo(newUserInfo);
        userRepository.save(user);
    }
}
