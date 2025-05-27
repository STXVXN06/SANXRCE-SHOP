package com.arpo.controller;

import com.arpo.config.CloudinaryConfig;
import com.arpo.models.CategoryProduct;
import com.arpo.models.Product;
import com.arpo.models.Supplier;
import com.arpo.service.CategoryProductService;
import com.arpo.service.ProductService;
import com.arpo.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryProductService categoryService;

    @Mock
    private SupplierService supplierService;

    @Mock
    private CloudinaryConfig cloudc;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;
    private CategoryProduct testCategory;
    private Supplier testSupplier;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        testCategory = new CategoryProduct();
        testCategory.setIdCategoryProduct(1L);
        testCategory.setNameCategory("Electronics");

        testSupplier = new Supplier();
        testSupplier.setIdSupplier(1L);
        testSupplier.setNameSupplier("Test Supplier");

        testProduct = new Product();
        testProduct.setIdProduct(1L);
        testProduct.setNameProduct("Test Product");
        testProduct.setStock(10);
        testProduct.setPrice(100.00);
        testProduct.setDescription("A test product");
        testProduct.setIdCategory(testCategory);
        testProduct.setIdSupplier(testSupplier);
        testProduct.setUrlImagen("http://example.com/image.jpg");

        // Mocking file upload
        when(mockFile.getBytes()).thenReturn("test-image-content".getBytes());
        Map<String, String> uploadResult = new HashMap<>();
        uploadResult.put("url", "http://newimage.com/new.jpg");
        when(cloudc.upload(eq("test-image-content".getBytes()), anyMap())).thenReturn(uploadResult);
    }

    @Test
    void saveProductAndImage() {
        when(categoryService.getById(eq(testCategory.getIdCategoryProduct()))).thenReturn(testCategory);
        when(supplierService.getById(eq(testSupplier.getIdSupplier()))).thenReturn(testSupplier);
        when(productService.saveProduct(eq(testProduct))).thenReturn(testProduct);

        String viewName = productController.saveProductAndImage(testProduct, bindingResult, model, mockFile);

        assertEquals("redirect:/product/registroProduct", viewName);

        verify(cloudc).upload(eq("test-image-content".getBytes()), anyMap());
        verify(categoryService).getById(eq(testCategory.getIdCategoryProduct()));
        verify(supplierService).getById(eq(testSupplier.getIdSupplier()));
        verify(productService).saveProduct(eq(testProduct));
    }

    @Test
    void deleteProduct() {
        when(productService.getByIdProduct(eq(testProduct.getIdProduct()))).thenReturn(testProduct);
        doNothing().when(productService).deleteProduct(eq(testProduct.getIdProduct()));
        when(productService.listProduct()).thenReturn(new ArrayList<>());

        String viewName = productController.deleteProduct(testProduct.getIdProduct(), model);

        assertEquals("redirect:/product/listProducts", viewName);
        verify(productService).deleteProduct(eq(testProduct.getIdProduct()));
    }

    @Test
    void updateProduct_ValidProductAndImage() {
        when(productService.getByIdProduct(eq(testProduct.getIdProduct()))).thenReturn(testProduct);
        when(productService.saveProduct(eq(testProduct))).thenReturn(testProduct);

        String viewName = productController.updateProduct(testProduct.getIdProduct(), testProduct, model, mockFile);

        assertEquals("redirect:/product/listProducts", viewName);
        verify(productService).getByIdProduct(eq(testProduct.getIdProduct()));
        verify(cloudc).upload(eq("test-image-content".getBytes()), anyMap());
        verify(productService).saveProduct(eq(testProduct));
        verify(model).addAttribute(eq("successMessage"), anyString());
    }

    @Test
    void listProducts() {
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        when(productService.listProduct()).thenReturn(products);

        String viewName = productController.listProducts(model);

        assertEquals("product/listProduct", viewName);
        verify(model).addAttribute(eq("ListProducts"), eq(products));
    }

    @Test
    void getActiveProduct() {
        List<Product> activeProducts = new ArrayList<>();
        activeProducts.add(testProduct);
        when(productService.getActiveProducts()).thenReturn(activeProducts);
        when(categoryService.listCategory()).thenReturn(new ArrayList<>());

        String viewName = productController.getActiveProduct(model);

        assertEquals("/exploreProducts", viewName);
        verify(model).addAttribute(eq("products"), eq(activeProducts));
    }

    @Test
    void filterProductsByCategory() {
        List<Product> filteredProducts = new ArrayList<>();
        filteredProducts.add(testProduct);
        when(productService.filterProductsByCategory(eq("Electronics"))).thenReturn(filteredProducts);
        when(categoryService.listCategory()).thenReturn(new ArrayList<>());

        String viewName = productController.filterProductsByCategory("Electronics", model);

        assertEquals("/exploreProductsCategories", viewName);
        verify(model).addAttribute(eq("products"), eq(filteredProducts));
    }
}
