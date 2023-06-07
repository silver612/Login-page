package com.example.demo.interceptor;

import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.service.AuthServiceImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggedInCheckerInterceptor implements HandlerInterceptor{
    
    @Autowired
    AuthServiceImpl authServiceImpl;

    @Override
    public boolean preHandle(
    HttpServletRequest request,
    HttpServletResponse response, 
    Object handler) throws Exception {

        if(request.getRequestURL().indexOf("/secure/")==-1)
            return true;
        if(request.getMethod().equals("OPTIONS"))
            return true;
        
        Cookie[] cookies = request.getCookies();

        if(cookies==null || cookies.length==0)
        {
            response.getWriter().write("Session Id Mismatch");
            response.setStatus(401);
            return false;
        }

        Optional<Cookie> usernameMaybe = Arrays.stream(cookies).filter(c -> "username".equals(c.getName())).findFirst();
        Optional<Cookie> sessionIdMaybe = Arrays.stream(cookies).filter(c -> "sessionId".equals(c.getName())).findFirst();

        if(usernameMaybe.isEmpty() || sessionIdMaybe.isEmpty())
        {
            response.getWriter().write("Session Id Mismatch");
            response.setStatus(401);
            return false;
        }
        String sessionId = sessionIdMaybe.get().getValue();
        String username = usernameMaybe.get().getValue();

        if(authServiceImpl.checkSessionId(username, sessionId))
            return true;
        else
        {
            response.getWriter().write("Session Id Mismatch");
            response.setStatus(401);
            return false;
        }
    }
}
