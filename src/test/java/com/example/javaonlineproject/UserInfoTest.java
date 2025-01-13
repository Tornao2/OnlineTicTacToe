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

// TU COS KOMBINOWALEM ALE NO  jest raczej zle
/*
package com.example.javaonlineproject;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static org.mockito.Mockito.*;

class UserInfoTest {
	@Mock
	private UserInfo user;
	@Mock
	private ServerSocket socket;
	@Mock
	Listener listener;
	@Mock
	Sender sender;
	@BeforeEach
	void setUp() {
    	MockitoAnnotations.openMocks(this);
   	// user.setUserSocket(socket);
   	// user.setUserInput(socket);
   	// user.setUserOutput(socket);
    	user = new UserInfo();
	//	socket = mock(Socket.class);
	}

	@Test
	void testSetAndGetUsername() {
    	String username = "testUser";
    	user.setUsername(username);
    	Assertions.assertEquals(username, user.getUsername());
	}

	@Test
	void testSetAndGetUserSocket() throws IOException {
    	user.setUserSocket(socket.accept());

    	verify(socket, times(1)).setSoTimeout(250);
    	Assertions.assertEquals(socket, user.getUserSocket());
	}

	@Test
	void testSetUserSocketHandlesNull() {
    	Assertions.assertDoesNotThrow(() -> user.setUserSocket(null));
    	Assertions.assertNull(user.getUserSocket());
	}

	@Test
	void testSetUserSocketHandlesSocketException() throws SocketException {
    	doThrow(new SocketException("Mock exception")).when(socket).setSoTimeout(250);

    	Assertions.assertDoesNotThrow(() -> user.setUserSocket(socket.accept()));
    	verify(socket, times(1)).setSoTimeout(250);
	}

	@Test
	void testCloseConnection() throws IOException {
    	user.setUserSocket(socket.accept());
    	user.setUserOutput(socket.accept());
    	user.setUserInput(socket.accept());

    	user.closeConnection();

    	verify(socket, times(1)).close();
    	Assertions.assertNull(user.getUserSocket());
	}

	@Test
	void testSetUserOutput() throws IOException{
    	user.setUserOutput(socket.accept());
    	Assertions.assertNotNull(sender);
	}

	@Test
	void testSetUserInput() throws IOException {
    	user.setUserInput(socket.accept());
    	listener = user.getUserInput();
    	Assertions.assertNotNull(listener);
	}
}


*/




