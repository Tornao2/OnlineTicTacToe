package com.example.javaonlineproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginScreenTest {

    private LoginScreen loginScreen;
//    private UserInfo user;
    @Mock
    private UserInfo user;
    @Mock
    private Socket socket;
    @Mock
    private OutputStream outputStream;
    private PrintWriter mockedWriter;
    @Mock
    private Listener listener;
    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        loginScreen = new LoginScreen();

        user.setUserSocket(socket);
        user.setUserInput(socket);
        user.setUserOutput(socket);
        loginScreen.setUser(user);
        mockedWriter = mock(PrintWriter.class);
        doNothing().when(mockedWriter).println(anyString());
    }
    @Test
    void testCorrectLogin() throws IOException {
        when(user.getUserSocket().isConnected()).thenReturn(true);
        when(user.getUserInput().receiveMessage()).thenReturn("ALLOWED");
        loginScreen.getUser().setUsername("validUser");
        loginScreen.getUser().getUserOutput().sendMessage("LOGIN,validUser,validPass");
        String response = loginScreen.getUser().getUserInput().receiveMessage();
        assertEquals("ALLOWED", response, "Login should be successful for correct credentials.");
    }

    @Test
    void testIncorrectLogin() throws IOException {
        when(user.getUserSocket().isConnected()).thenReturn(true);
        when(user.getUserInput().receiveMessage()).thenReturn("WRONGPASSWORD");
        loginScreen.getUser().setUsername("validUser");
        loginScreen.getUser().getUserOutput().sendMessage("LOGIN,validUser,wrongPass");
        String response = loginScreen.getUser().getUserInput().receiveMessage();
        assertEquals("WRONGPASSWORD", response, "Login should fail for incorrect password.");
    }

    @Test
    void testCorrectRegister() throws IOException {
        when(user.getUserSocket().isConnected()).thenReturn(true);
        when(user.getUserInput().receiveMessage()).thenReturn("ALLOWED");
        loginScreen.getUser().setUsername("newUser");
        loginScreen.getUser().getUserOutput().sendMessage("SIGNUP,newUser,newPass");
        String response = loginScreen.getUser().getUserInput().receiveMessage();
        assertEquals("ALLOWED", response, "Registration should succeed for valid data.");
    }

    @Test
    void testRegisterUsernameAlreadyTaken() throws IOException {
        when(user.getUserSocket().isConnected()).thenReturn(true);
        when(user.getUserInput().receiveMessage()).thenReturn("USERNAMEEXISTS");
        loginScreen.getUser().setUsername("2");
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
