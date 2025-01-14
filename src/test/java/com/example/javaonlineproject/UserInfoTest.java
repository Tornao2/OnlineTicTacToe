package com.example.javaonlineproject;

import org.junit.jupiter.api.*;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static org.mockito.Mockito.*;

class UserInfoTest {
    @Mock
    private UserInfo userInfo;
    @Mock
    private Socket mockSocket;

    @BeforeEach
    void setUp() {
        userInfo = new UserInfo();
        mockSocket = mock(Socket.class);
    }
    @Test
    void testSetAndGetUsername() {
        String username = "testUser";
        userInfo.setUsername(username);
        Assertions.assertEquals(username, userInfo.getUsername());
    }

    @Test
    void testSetAndGetUserSocket() throws SocketException {
        userInfo.setUserSocket(mockSocket);
        verify(mockSocket, times(1)).setSoTimeout(250);
        Assertions.assertEquals(mockSocket, userInfo.getUserSocket());
    }

    @Test
    void testSetUserSocketHandlesNull() {
        Assertions.assertDoesNotThrow(() -> userInfo.setUserSocket(null));
        Assertions.assertNull(userInfo.getUserSocket());
    }

    @Test
    void testSetUserSocketHandlesSocketException() throws SocketException {
        doThrow(new SocketException("Mock exception")).when(mockSocket).setSoTimeout(250);
        Assertions.assertDoesNotThrow(() -> userInfo.setUserSocket(mockSocket));
        verify(mockSocket, times(1)).setSoTimeout(250);
    }

    @Test
    void testCloseConnection() throws IOException {
        ByteArrayOutputStream fakeOutputStream = new ByteArrayOutputStream();
        try {
            when(mockSocket.getOutputStream()).thenReturn(fakeOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteArrayInputStream fakeInputStream = new ByteArrayInputStream("Received data".getBytes());
        try {
            when(mockSocket.getInputStream()).thenReturn(fakeInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userInfo.setUserSocket(mockSocket);
        userInfo.setUserOutput(mockSocket);
        userInfo.setUserInput(mockSocket);
        userInfo.closeConnection();
        verify(mockSocket, times(1)).close();
        Assertions.assertNull(userInfo.getUserSocket());
    }

    @Test
    void testSetUserOutput() {
        ByteArrayOutputStream fakeOutputStream = new ByteArrayOutputStream();
        try {
            when(mockSocket.getOutputStream()).thenReturn(fakeOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userInfo.setUserOutput(mockSocket);
        Sender sender = userInfo.getUserOutput();
        Assertions.assertNotNull(sender);
    }

    @Test
    void testSetUserInput() {
        ByteArrayInputStream fakeInputStream = new ByteArrayInputStream("Received data".getBytes());
        try {
            when(mockSocket.getInputStream()).thenReturn(fakeInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userInfo.setUserInput(mockSocket);
        Listener listener = userInfo.getUserInput();
        Assertions.assertNotNull(listener);
    }
}
