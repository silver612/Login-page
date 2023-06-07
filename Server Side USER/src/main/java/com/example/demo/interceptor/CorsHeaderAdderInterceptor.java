package com.example.demo.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CorsHeaderAdderInterceptor implements HandlerInterceptor{
    
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response, 
        Object handler) throws Exception {
            
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Methods", "PUT, POST, GET, PATCH, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "content-type");
            return true;
        }
}
