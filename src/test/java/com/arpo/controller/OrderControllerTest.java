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
 import com.arpo.service.OrderService;
 import com.arpo.service.UserService;

 import java.io.ByteArrayOutputStream;
 import java.io.FileOutputStream; // Import FileOutputStream
 import java.io.File;             // Import File
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import java.util.Optional;

 import static org.junit.jupiter.api.Assertions.assertEquals;
 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.ArgumentMatchers.anyLong;
 import static org.mockito.ArgumentMatchers.eq;
 import static org.mockito.ArgumentMatchers.contains;
 import static org.mockito.Mockito.*;

 @SpringBootTest
 class OrderControllerTest {

     @MockBean
     private OrderService orderService;

     @MockBean
     private UserService userService;

     @Mock
     private HttpServletResponse response;

     @Mock
     private HttpSession session;

     @Mock
     private Model model;

     @Autowired
     private OrderController orderController;

     private User testUser;
     private Order testOrder;
     private List<Cart> testCartDetails;
     private Product testProduct;
     private CategoryProduct testCategory;
     private ByteArrayOutputStream outputStreamCaptor; // Declare captor at class level

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
     void setUp() throws IOException {
         MockitoAnnotations.openMocks(this);

         testUser = new User();
         testUser.setIdUser(12345L);
         testUser.setName("John");
         testUser.setSurname("Doe");
         testUser.setEmail("john.doe@example.com");
         testUser.setAddress("123 Main St");
         Rol clientRole = new Rol();
         clientRole.setId_Rol(3);
         clientRole.setName_rol("Cliente");
         testUser.setIdRol(clientRole);

         testCategory = new CategoryProduct();
         testCategory.setIdCategoryProduct(1L);
         testCategory.setNameCategory("Electronics");

         testProduct = new Product();
         testProduct.setIdProduct(101L);
         testProduct.setNameProduct("Laptop");
         testProduct.setPrice(1200.00);
         testProduct.setStock(5);
         testProduct.setDescription("Powerful laptop");
         testProduct.setIdCategory(testCategory);

         testCartDetails = new ArrayList<>();
         Cart cartItem1 = new Cart();
         cartItem1.setIdcart(1L);
         cartItem1.setNombre("Laptop");
         cartItem1.setCantidad(1);
         cartItem1.setPrecio(1200.00);
         cartItem1.setTotal(1200.00);
         cartItem1.setProduct(testProduct);
         testCartDetails.add(cartItem1);

         Cart cartItem2 = new Cart();
         cartItem2.setIdcart(2L);
         cartItem2.setNombre("Mouse");
         cartItem2.setCantidad(2);
         cartItem2.setPrecio(25.00);
         cartItem2.setTotal(50.00);
         cartItem2.setProduct(new Product());
         testCartDetails.add(cartItem2);

         testOrder = new Order();
         testOrder.setIdOrder(1);
         testOrder.setDateOrder(new Date());
         testOrder.setStatus("Procesado");
         testOrder.setTotal(1250.00);
         testOrder.setUser(testUser);
         testOrder.setDetalle(testCartDetails);

         // Initialize the captor for the output stream
         outputStreamCaptor = new ByteArrayOutputStream();
         MockServletOutputStream mockOutputStream = new MockServletOutputStream(outputStreamCaptor);
         when(response.getOutputStream()).thenReturn(mockOutputStream);

         when(response.getWriter()).thenReturn(new PrintWriter(new ByteArrayOutputStream()));
     }

     @Test
     void exportToPDF_OrderFound_GeneratesPdf() throws Exception {
         int orderId = 1;
         when(orderService.findById(orderId)).thenReturn(Optional.of(testOrder));

         orderController.exportToPDF(orderId, response);

         verify(response).setContentType("application/pdf");
         verify(response).setHeader(eq("Content-Disposition"), contains("filename=factura_John_1.pdf"));
         verify(response).getOutputStream();
         verify(response, never()).getWriter();

         // Save the captured PDF content to a file
         File pdfFile = new File("generated_order_" + orderId + ".pdf");
         try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
             outputStreamCaptor.writeTo(fos);
             System.out.println("PDF saved to: " + pdfFile.getAbsolutePath());
         }
     }

 }