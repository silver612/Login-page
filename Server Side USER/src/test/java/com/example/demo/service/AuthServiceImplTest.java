package com.example.demo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.data.models.User;
import com.example.demo.data.payloads.UserAuth;
import com.example.demo.data.repository.UserRepository;
import com.example.demo.exception.ResourceNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthServiceImplTest {
    
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private User user;

    @MockBean
    private UserAuth userAuth;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Captor
    ArgumentCaptor<User> userToRepo;

    private User giveUser(String username, Long sessionId, String password, String info) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setSessionId(sessionId);
        newUser.setPassword(password);
        newUser.setUserInfo(info);
        return newUser;
    }

    private UserAuth giveUserAuth(String username, String password, String newusername, String newpassword) {
        return new UserAuth(username, password, newusername, newpassword);
    }

    @Test
    public void checkValidityOfPassword_ReturnSessionId_ValidUser() {
        
        User user1 = giveUser("", 0L, "password", null);
        when(userRepository.findByName(any(String.class))).thenReturn(Optional.of(user1));
        UserAuth userAuth1 = giveUserAuth("", "password", "", "");
        assertNotNull(authServiceImpl.checkValidityOfPassword(userAuth1));   
    }

    @Test
    public void checkValidityOfPassword_ReturnNull_IncorrectPassword() {
        
        User user1 = giveUser("", 0L, "password_correct", null);
        when(userRepository.findByName(any(String.class))).thenReturn(Optional.of(user1));
        UserAuth userAuth1 = giveUserAuth("", "password_incorrect", "", "");
        assertEquals(authServiceImpl.checkValidityOfPassword(userAuth1), null);   
    }

    @Test
    public void checkValidityOfPassword_ThrowException_UserNotFound() {
        
        when(userRepository.findByName(any(String.class))).thenReturn(Optional.empty());
        UserAuth userAuth1 = giveUserAuth("test_username", "", "", "");
        assertThrowsExactly(ResourceNotFoundException.class, () -> authServiceImpl.checkValidityOfPassword(userAuth1));   
    }

    @Test
    public void checkSessionId_ReturnsTrue_ValidSessionId() {
        User user1 = giveUser("", 1000L, "", null);
        when(userRepository.findByName(any(String.class))).thenReturn(Optional.of(user1));
        Long x = 1000L;
        System.out.println(x.toString());
        assertEquals(authServiceImpl.checkSessionId("", "1000"), true);
    }

    @Test
    public void checkSessionId_ReturnsFalse_InvalidSessionId() {
        User user1 = giveUser("", 1000L, "", null);
        when(userRepository.findByName(any(String.class))).thenReturn(Optional.of(user1));
        assertEquals(authServiceImpl.checkSessionId("", "2000"), false);
    }

    @Test
    public void checkSessionId_ThrowException_UserNotFound() {
        
        when(userRepository.findByName(any(String.class))).thenReturn(Optional.empty());
        assertThrowsExactly(ResourceNotFoundException.class, () -> authServiceImpl.checkSessionId("", ""));   
    }

    @Test
    public void createUser_CreatesUserAndReturnsSessionId_ValidUserCreation() {
        
        UserAuth userAuth1 = giveUserAuth("test_username", "test_password", "", "");
        when(userRepository.findByName("test_username")).thenReturn(Optional.empty());
        
        doReturn(new User()).when(userRepository).save(any());
        authServiceImpl.createUser(userAuth1);

        verify(userRepository).save(userToRepo.capture());

        User capturedUser = userToRepo.getValue();
        assertEquals(capturedUser.getUsername(), userAuth1.getUsername());
        assertEquals(capturedUser.getPassword(), userAuth1.getPassword());
    }

    @Test
    public void createUser_ReturnsAlreadyExists_UsernameExists() {
        UserAuth userAuth1 = giveUserAuth("test_username", "test_password", "", "");
        User user1 = giveUser("test_username", 0L, "", null);
        
        when(userRepository.findByName("test_username")).thenReturn(Optional.of(user1));
        String result = authServiceImpl.createUser(userAuth1);

        assertEquals(result, "ALREADY_EXISTS");
    }

    @Test
    public void deleteUser_DeletesUser_UserExists() {
        User user1 = giveUser("test_username", 0L, "", null);
        
        when(userRepository.findByName("test_username")).thenReturn(Optional.of(user1));
        doNothing().when(userRepository).deleteById(any());
        authServiceImpl.deleteUser(user1.getUsername());

        verify(userRepository).deleteById(user1.getId());
    }

    @Test 
    public void deleteUser_ThrowsException_UsernameDoesNotExist() {
        when(userRepository.findByName(any())).thenReturn(Optional.empty());
        assertThrowsExactly(ResourceNotFoundException.class, () -> {authServiceImpl.deleteUser("test_username");});
    }

    @Test
    public void updateUsername_ReturnsAlreadyExists_NewUsernameAlreadyTaken() {
        User user1 = giveUser("test_username", 0L, "", null);
        User user2 = giveUser("test_new_username", 0L, "", null);
        
        when(userRepository.findByName("test_username")).thenReturn(Optional.of(user1));
        when(userRepository.findByName("test_new_username")).thenReturn(Optional.of(user2));
        
        assertEquals(authServiceImpl.updateUsername("test_username", "test_new_username"), "ALREADY_EXISTS");
    }

    @Test
    public void updateUsername_ReturnsOk_NewUsernameAvailable() {
        User user1 = giveUser("test_username", 0L, "", null);
        
        when(userRepository.findByName("test_username")).thenReturn(Optional.of(user1));
        when(userRepository.findByName("test_new_username")).thenReturn(Optional.empty());
        
        doReturn(new User()).when(userRepository).save(any());
        assertEquals(authServiceImpl.updateUsername("test_username", "test_new_username"), "OK");
        verify(userRepository).save(any());
    }

    @Test
    public void updateUsername_ThrowsException_UserNotFound() {
        when(userRepository.findByName("test_username")).thenReturn(Optional.empty());
        assertThrowsExactly(ResourceNotFoundException.class, () -> {authServiceImpl.updateUsername("test_username", "test_new_username");});
    }

    @Test
    public void updatePassword_UpdatesPassword_UserFound() {
        UserAuth userAuth1 = giveUserAuth("test_username", "", "", "test_new_password");
        User user1 = giveUser("test_username", 0L, "", null);
        
        when(userRepository.findByName("test_username")).thenReturn(Optional.of(user1));
        doReturn(new User()).when(userRepository).save(any());
        
        authServiceImpl.updatePassword(userAuth1.getUsername(), userAuth1);

        verify(userRepository).save(userToRepo.capture());

        User capturedUser = userToRepo.getValue();
        assertEquals(capturedUser.getPassword(), userAuth1.getNewPassword());
    }

    @Test
    public void updatePassword_ThrowsException_UserNotFound() {
        UserAuth userAuth1 = giveUserAuth("test_username", "", "", "");
        when(userRepository.findByName("test_username")).thenReturn(Optional.empty());
        assertThrowsExactly(ResourceNotFoundException.class, () -> {authServiceImpl.updatePassword(userAuth1.getUsername(), userAuth1);});
    }

    @Test
    public void getUserInfo_ReturnsUserInfo_UserFound() {
        User user1 = giveUser("test_username", 0L, "", "test_info");

        when(userRepository.findByName("test_username")).thenReturn(Optional.of(user1));
        assertEquals(authServiceImpl.getUserInfo(user1.getUsername()), user1);
    }

    @Test
    public void getUserInfo_ThrowsException_UserNotFound() {
        when(userRepository.findByName("")).thenReturn(Optional.empty());
        assertThrowsExactly(ResourceNotFoundException.class, () -> {authServiceImpl.getUserInfo("");});
    }

    @Test
    public void setUserInfo_SetsInfo_UserFound() {
        User user1 = giveUser("test_username", 0L, "", "test_info");

        when(userRepository.findByName("test_username")).thenReturn(Optional.of(user1));
        doReturn(new User()).when(userRepository).save(any());
        
        authServiceImpl.setUserInfo(user1.getUsername(), "new_user_info");

        verify(userRepository).save(userToRepo.capture());

        User capturedUser = userToRepo.getValue();
        assertEquals(capturedUser.getUserInfo(), "new_user_info");
    }

    @Test
    public void setUserInfo_ThrowsException_UserNotFound() {
        when(userRepository.findByName("")).thenReturn(Optional.empty());
        assertThrowsExactly(ResourceNotFoundException.class, () -> {authServiceImpl.setUserInfo("", null);});
    }
}
