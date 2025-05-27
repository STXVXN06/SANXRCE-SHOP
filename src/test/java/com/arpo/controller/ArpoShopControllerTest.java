package com.arpo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.arpo.models.Rol;
import com.arpo.models.Product;
import com.arpo.models.CategoryProduct;
import com.arpo.service.CategoryProductService;
import com.arpo.service.ProductService;
import com.arpo.service.UserService;
import com.arpo.service.RolService;
import com.arpo.singleton.Singleton;

@SpringBootTest
class ArpoShopControllerTest {

    @MockBean
    private Singleton userSingleton;

    @MockBean
    private UserService userService;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryProductService categoryService;

    @MockBean
    private RolService rolService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Autowired
    private ArpoShopController arpoShopController;

    private User testUser;
    private Rol testRolAdmin;
    private Rol testRolClient;
    private ArrayList<Product> testProducts;
    private ArrayList<CategoryProduct> testCategories;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setIdUser(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setAge(30);
        testUser.setAddress("123 Test St");
        testUser.setPhoneNumber("1234567890");

        testRolAdmin = new Rol();
        testRolAdmin.setId_Rol(1);
        testRolAdmin.setName_rol("Admin");

        testRolClient = new Rol();
        testRolClient.setId_Rol(3);
        testRolClient.setName_rol("Cliente");

        testUser.setIdRol(testRolAdmin); // default

        testProducts = new ArrayList<>();
        Product product1 = new Product();
        product1.setIdProduct(1L);
        product1.setNameProduct("Test Product 1");
        testProducts.add(product1);

        testCategories = new ArrayList<>();
        CategoryProduct category1 = new CategoryProduct();
        category1.setIdCategoryProduct(1L);
        category1.setNameCategory("Test Category 1");
        testCategories.add(category1);

        when(productService.getActiveProducts()).thenReturn(testProducts);
        when(categoryService.listCategory()).thenReturn(testCategories);

        // Respuesta dinámica del mock para el login
        when(userSingleton.login(anyString(), anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            String password = invocation.getArgument(1);
            if (email.equals("test@example.com") && password.equals("password")) {
                return Optional.of(testUser);
            }
            return Optional.empty();
        });
    }

    @Test
    void login_ValidCredentialsAdmin_ReturnsExploreProductsView() {
        testUser.setIdRol(testRolAdmin);

        String viewName = arpoShopController.login("test@example.com", "password", model, session);

        assertEquals("exploreProducts", viewName);
        verify(session).setAttribute(eq("userId"), eq(1L));
        verify(model).addAttribute(eq("user"), eq(testUser));
        verify(productService).getActiveProducts();
        verify(categoryService).listCategory();
    }

    @Test
    void login_ValidCredentialsClient_ReturnsClientTemplateView() {
        testUser.setIdRol(testRolClient);

        String viewName = arpoShopController.login("test@example.com", "password", model, session);

        assertEquals("client/clientTemplate", viewName);
        verify(session).setAttribute(eq("userId"), eq(1L));
        verify(model).addAttribute(eq("user"), eq(testUser));
        verify(productService).getActiveProducts();
        verify(categoryService).listCategory();
    }
}
