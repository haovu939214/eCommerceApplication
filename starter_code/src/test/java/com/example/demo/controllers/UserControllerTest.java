package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUser() throws Exception {
        when(encoder.encode("passwordTest")).thenReturn("Hashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("passwordTest");
        createUserRequest.setConfirmPassword("passwordTest");
        final ResponseEntity<User> responseUser = userController.createUser(createUserRequest);
        assertNotNull(responseUser);
        assertEquals(200, responseUser.getStatusCodeValue());
        User user = responseUser.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("Hashed", user.getPassword());
    }

    @Test
    public void testFindUserByUserName() throws Exception {
        User userFake = getUser();
        when(userRepository.findByUsername("testUser")).thenReturn(userFake);
        final ResponseEntity<User> responseUser = userController.findByUserName("testUser");
        User user = responseUser.getBody();
        assertNotNull(user);
        assertEquals(userFake.getUsername(), user.getUsername());
    }

    @Test
    public void testFindUserByUserNameNotExists() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        final ResponseEntity<User> responseUser = userController.findByUserName("testUser");
        User user = responseUser.getBody();
        assertNull(user);
    }

    @Test
    public void testCreateUserNameExists() throws Exception {
        User userFake = getUser();
        when(userRepository.findByUsername("testUser")).thenReturn(userFake);

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("passwordTest");

        thrown.expect(Exception.class);
        thrown.expectMessage("Username is exist");
        userController.createUser(createUserRequest);
    }

    @Test
    public void testCreateUserPasswordNotSame() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("passwordTest");
        createUserRequest.setConfirmPassword("passwordTest1");

        thrown.expect(Exception.class);
        thrown.expectMessage("Confirm password not mapping");
        userController.createUser(createUserRequest);
    }

    @Test
    public void testCreateUserPasswordLengthNotMin() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser1");
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("test");

        thrown.expect(Exception.class);
        thrown.expectMessage("Password less more 7 characters");
        userController.createUser(createUserRequest);
    }

    @Test
    public void testFindUserById() throws Exception {
        User userFake = getUser();
        when(userRepository.findById(0L)).thenReturn(Optional.of((userFake)));
        final ResponseEntity<User> responseUser = userController.findById(0L);
        User user = responseUser.getBody();
        assertNotNull(user);
        assertEquals(userFake.getUsername(), user.getUsername());
    }

    @Test
    public void testFindUserByIdNotExists() throws Exception {
        final ResponseEntity<User> responseUser = userController.findById(1L);
        assertNotNull(responseUser);
        assertEquals(404, responseUser.getStatusCodeValue());
    }

    private User getUser() {
        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("testUser");
        user.setPassword("passwordTest");
        user.setCart(cart);
        return user;
    }
}
