package com.arpo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.arpo.models.User;
import com.arpo.service.ProductService; // Import ProductService
import com.arpo.service.UserService;
import com.arpo.singleton.Singleton;

@SpringBootTest  //  ADDED Spring Boot Test annotation
class ArpoShopControllerTest {

    @MockBean
    private Singleton userSingleton;

    @MockBean
    private UserService userService;

    @MockBean
    private ProductService productService;  //  ADDED MockBean for ProductService

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Autowired   //  CHANGED to Autowired
    private ArpoShopController arpoShopController;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
    }

    @Test
    void login_ValidCredentials_ReturnsCorrectView() {
        when(userSingleton.login("test@example.com", "password")).thenReturn(Optional.of(testUser));
        //  MOCK ProductService behavior (if needed for this test)
        when(productService.getActiveProducts()).thenReturn(new ArrayList<>()); // Or whatever you need to return
        
        String viewName = arpoShopController.login("test@example.com", "password", model, session);
        assertEquals("exploreProducts", viewName);
        verify(session).setAttribute(eq("userId"), anyLong());
    }

    @Test
    void login_InvalidCredentials_ReturnsLoginViewWithError() {
        when(userSingleton.login("test@example.com", "password")).thenReturn(Optional.empty());
        String viewName = arpoShopController.login("test@example.com", "password", model, session);
        assertEquals("login", viewName);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void signIn_ReturnsSignInView() {
        String viewName = arpoShopController.signIn(model);
        assertEquals("signIn", viewName);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void logout_InvalidatesSessionAndRedirectsToLogin() {
        String viewName = arpoShopController.logout(session);
        assertEquals("redirect:/", viewName);
        verify(session).invalidate();
    }
}