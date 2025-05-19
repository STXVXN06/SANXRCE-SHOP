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
 import com.arpo.singleton.Singleton;

 @SpringBootTest //  ADDED Spring Boot Test annotation
 class ArpoShopControllerTest {

     @MockBean
     private Singleton userSingleton;

     @MockBean
     private UserService userService;

     @MockBean
     private ProductService productService; //  ADDED MockBean for ProductService
     
     @MockBean
     private CategoryProductService categoryService;

     @Mock
     private Model model;

     @Mock
     private HttpSession session;

     @Autowired
     private ArpoShopController arpoShopController;

     private User testUser;
     private Rol testRol;
     private ArrayList<Product> testProducts;
     private ArrayList<CategoryProduct> testCategories;
     

     @BeforeEach
     void setUp() {

         MockitoAnnotations.openMocks(this); // Initialize Mockito mocks

         testUser = new User();
         testUser.setIdUser(1L);
         testUser.setEmail("test@example.com");
         testUser.setPassword("password");

         testRol = new Rol();
         testRol.setId_Rol(1);
         testRol.setName_rol("Admin");
         testUser.setIdRol(testRol);

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
     }

     @Test
     void login_ValidCredentials_ReturnsCorrectView() {
         when(userSingleton.login("test@example.com", "password")).thenReturn(Optional.of(testUser));
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