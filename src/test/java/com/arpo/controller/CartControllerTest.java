package com.arpo.controller;

 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.mockito.Mock;
 import org.mockito.MockitoAnnotations;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.boot.test.mock.mockito.MockBean;
 import org.springframework.ui.Model;
 import jakarta.servlet.http.HttpServletResponse;
 import jakarta.servlet.http.HttpSession;
 import jakarta.servlet.ServletOutputStream;
 import jakarta.servlet.WriteListener;


 import com.arpo.models.Cart;
 import com.arpo.models.CategoryProduct;
 import com.arpo.models.Order;
 import com.arpo.models.Product;
 import com.arpo.models.Rol;
 import com.arpo.models.User;
 import com.arpo.service.CartService;
 import com.arpo.service.OrderService;
 import com.arpo.service.ProductService;
 import com.arpo.service.UserService;
 import com.arpo.service.CategoryProductService;
 import com.arpo.singleton.Singleton;

 import java.io.ByteArrayOutputStream;
 import java.io.FileOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.lang.reflect.Field;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import java.util.Optional;


 import static org.junit.jupiter.api.Assertions.assertEquals;
 import static org.junit.jupiter.api.Assertions.assertTrue;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.ArgumentMatchers.anyDouble;
 import static org.mockito.ArgumentMatchers.anyInt;
 import static org.mockito.ArgumentMatchers.anyList;
 import static org.mockito.ArgumentMatchers.anyLong;
 import static org.mockito.ArgumentMatchers.anyString;
 import static org.mockito.ArgumentMatchers.eq;
 import static org.mockito.ArgumentMatchers.contains;
 import static org.mockito.Mockito.never;
 import static org.mockito.Mockito.times;
 import static org.mockito.Mockito.verify;
 import static org.mockito.Mockito.when;

 @SpringBootTest
 class CartControllerTest {

     @MockBean
     private CartService cartService;

     @MockBean
     private ProductService productService;

     @MockBean
     private UserService userService;

     @MockBean
     private OrderService orderService;

     @Mock
     private Model model;

     @Mock
     private HttpSession session;

     @Mock
     private HttpServletResponse response;

     @Autowired
     private CartController cartController;

     private Product testProduct;
     private User testUser;
     private ByteArrayOutputStream outputStreamCaptor;

     private static class MockServletOutputStream extends ServletOutputStream {
         private ByteArrayOutputStream baos;

         public MockServletOutputStream(ByteArrayOutputStream baos) {
             this.baos = baos;
         }

         @Override
         public void write(int b) throws IOException {
             baos.write(b);
         }

         @Override
         public void write(byte[] b) throws IOException {
             baos.write(b);
         }

         @Override
         public void write(byte[] b, int off, int len) throws IOException {
             baos.write(b, off, len);
         }

         @Override
         public boolean isReady() {
             return true;
         }

         @Override
         public void setWriteListener(WriteListener writeListener) {
             // Not used in this simple mock
         }
     }

     @BeforeEach
     void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {
         MockitoAnnotations.openMocks(this);

         // Reset internal state of CartController for each test due to singleton nature
         Field detallesField = CartController.class.getDeclaredField("detalles");
         detallesField.setAccessible(true);
         ((ArrayList<Cart>) detallesField.get(cartController)).clear();

         Field orderField = CartController.class.getDeclaredField("order");
         orderField.setAccessible(true);
         orderField.set(cartController, new Order()); // Re-initialize order

         // Set up fake product data
         testProduct = new Product();
         testProduct.setIdProduct(1L);
         testProduct.setNameProduct("Test Product");
         testProduct.setPrice(100.00);
         testProduct.setStock(10); // Available stock

         // Set up fake user data
         testUser = new User();
         testUser.setIdUser(100L);
         testUser.setName("Test User");

         // For PDF related tests in OrderControllerTest (if present in this merged file)
         outputStreamCaptor = new ByteArrayOutputStream();
         MockServletOutputStream mockOutputStream = new MockServletOutputStream(outputStreamCaptor);
         when(response.getOutputStream()).thenReturn(mockOutputStream);
         when(response.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
     }

     @Test
     void addCart_ValidQuantity_AddsToCartAndReturnsView() {
         Long productId = 1L;
         Integer quantity = 2;
         when(productService.get(productId)).thenReturn(Optional.of(testProduct));

         String viewName = cartController.addCart(productId, quantity, model);

         assertEquals("cart/view-cart", viewName);
         verify(model).addAttribute(eq("cart"), anyList());
         verify(model).addAttribute(eq("order"), any(Order.class));
     }

 


     @Test
     void saveOrder_SavesOrderAndClearsCart() throws NoSuchFieldException, IllegalAccessException {
         when(session.getAttribute("userId")).thenReturn(testUser.getIdUser());
         when(userService.findById(testUser.getIdUser())).thenReturn(Optional.of(testUser));
         when(orderService.save(any(Order.class))).thenReturn(new Order());
         when(cartService.save(any(Cart.class))).thenReturn(new Cart());

         List<Product> availableProducts = new ArrayList<>();
         Product p1 = new Product();
         p1.setIdProduct(1L); p1.setStock(10); p1.setPrice(100.0); p1.setNameProduct("Product1");
         Product p2 = new Product();
         p2.setIdProduct(2L); p2.setStock(5); p2.setPrice(50.0); p2.setNameProduct("Product2");
         availableProducts.add(p1);
         availableProducts.add(p2);
         when(productService.listProduct()).thenReturn(availableProducts);

         Cart cartItem1 = new Cart();
         cartItem1.setProduct(p1); cartItem1.setCantidad(2); cartItem1.setNombre("Product1");
         Cart cartItem2 = new Cart();
         cartItem2.setProduct(p2); cartItem2.setCantidad(1); cartItem2.setNombre("Product2");

         Field detallesField = CartController.class.getDeclaredField("detalles");
         detallesField.setAccessible(true);
         ((ArrayList<Cart>) detallesField.get(cartController)).add(cartItem1);
         ((ArrayList<Cart>) detallesField.get(cartController)).add(cartItem2);

         Field orderField = CartController.class.getDeclaredField("order");
         orderField.setAccessible(true);
         Order currentOrder = new Order();
         currentOrder.setTotal(250.0);
         currentOrder.setUser(testUser);
         orderField.set(cartController, currentOrder);

         String viewName = cartController.saveOrder(session);

         assertEquals("redirect:/order/showOrders", viewName);
         verify(orderService, times(1)).save(any(Order.class));
         verify(cartService, times(2)).save(any(Cart.class));
         verify(productService, times(1)).listProduct();

         assertTrue(((ArrayList<Cart>) detallesField.get(cartController)).isEmpty());
         assertTrue(((Order) orderField.get(cartController)).getTotal() == 0);
     }
 }