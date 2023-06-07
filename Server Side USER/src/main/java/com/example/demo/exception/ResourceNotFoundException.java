package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException{
    
    public ResourceNotFoundException(String username){
        super("User not found with " + username + " username");
    }

    public HttpStatus getStatus(){
        return HttpStatus.NOT_FOUND;
    }
}
