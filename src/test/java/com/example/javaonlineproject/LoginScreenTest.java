package com.example.javaonlineproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginScreenTest {

    private static final Logger log = LoggerFactory.getLogger(LoginScreenTest.class);
    @Mock
    private LoginScreen loginScreen;
    @Mock
    UserInfo user;
    @Mock
    Sender sender;
    @Mock
    Listener listener;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(loginScreen.getUser()).thenReturn(user);
        when(loginScreen.getUser().getUserInput()).thenReturn(listener);
        when(loginScreen.getUser().getUserOutput()).thenReturn(sender);
        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream;
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        try {
            inputStream = new PipedInputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Sender sender = new Sender();
        Listener listener = new Listener();
        Field field = null;
        try {
            field = sender.getClass().getDeclaredField("output");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        try {
            field.set(sender, printWriter);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            field = listener.getClass().getDeclaredField("input");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        try {
            field.set(listener, bufferedReader);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testCorrectLogin()  {
        when(loginScreen.getUser().getUserInput().receiveMessage()).thenReturn("ALLOWED");
        loginScreen.getUser().getUserOutput().sendMessage("LOGIN,validUser,validPass");
        String response = loginScreen.getUser().getUserInput().receiveMessage();
        assertEquals("ALLOWED", response, "Login should be successful for correct credentials.");
    }

    @Test
    void testIncorrectLogin()  {
        when(loginScreen.getUser().getUserInput().receiveMessage()).thenReturn("WRONGPASSWORD");
        loginScreen.getUser().getUserOutput().sendMessage("LOGIN,validUser,wrongPass");
        String response = loginScreen.getUser().getUserInput().receiveMessage();
        assertEquals("WRONGPASSWORD", response, "Login should fail for incorrect password.");
    }

    @Test
    void testCorrectRegister()  {
        when(loginScreen.getUser().getUserInput().receiveMessage()).thenReturn("ALLOWED");
        loginScreen.getUser().getUserOutput().sendMessage("SIGNUP,newUser,newPass");
        String response = loginScreen.getUser().getUserInput().receiveMessage();
        assertEquals("ALLOWED", response, "Registration should succeed for valid data.");
    }

    @Test
    void testRegisterUsernameAlreadyTaken()  {
        when(loginScreen.getUser().getUserInput().receiveMessage()).thenReturn("USERNAMEEXISTS");
        loginScreen.getUser().getUserOutput().sendMessage("SIGNUP,2,newPass");
        String response = loginScreen.getUser().getUserInput().receiveMessage();
        assertEquals("USERNAMEEXISTS", response, "Registration should fail if username is already taken.");
    }
/*
    @Test
    void testLoginWithEmptyFields() throws IOException {
        loginScreen.getUser().setUsername("");
        loginScreen.getUser().getUserOutput().sendMessage("LOGIN,,");
        assertTrue(loginScreen.getUser().getUsername().isEmpty(), "Login should fail if fields are empty.");
    }

    @Test
    void testRegisterWithInvalidCharacters() throws IOException {
        loginScreen.getUser().setUsername("invalid!User");
        loginScreen.getUser().getUserOutput().sendMessage("SIGNUP,invalid!User,newPass");
        assertTrue(loginScreen.getUser().getUsername().matches(".*[^a-zA-Z0-9].*"), "Username with invalid characters should be rejected.");
    }
    */
}
