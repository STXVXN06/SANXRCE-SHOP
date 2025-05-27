package com.arpo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.arpo.config.CloudinaryConfig;
import com.arpo.models.CategoryProduct;
import com.arpo.models.Product;
import com.arpo.models.Supplier;
import com.arpo.service.CategoryProductService;
import com.arpo.service.ProductService;
import com.arpo.service.SupplierService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryProductService categoryService;

    @Mock
    private SupplierService supplierService;

    @Mock
    private Model model;

    @Mock
    private CloudinaryConfig cloudinaryConfig;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;
    private CategoryProduct testCategory;
    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = new CategoryProduct();
        testCategory.setIdCategoryProduct(1L);
        testCategory.setNameCategory("Electronics");

        testSupplier = new Supplier();
        testSupplier.setIdSupplier(1L);
        testSupplier.setNameSupplier("Tech Supplier");

        testProduct = new Product();
        testProduct.setIdProduct(1L);
        testProduct.setNameProduct("Laptop");
        testProduct.setPrice(1200.0);
        testProduct.setStock(10);
        testProduct.setIdCategory(testCategory);
        testProduct.setIdSupplier(testSupplier);
    }

    @Test
    void listProducts_ReturnsProductListView() {
        List<Product> productList = new ArrayList<>();
        productList.add(testProduct);
        when(productService.listProduct()).thenReturn(productList);

        String viewName = productController.listProducts(model);

        assertEquals("product/listProduct", viewName);
        verify(model).addAttribute(eq("ListProducts"), eq(productList));
    }

    @Test
    void createProduct_ReturnsCreateProductView() {

        when(categoryService.listCategory()).thenReturn(List.of(testCategory));
        when(supplierService.listSuppliers()).thenReturn(List.of(testSupplier));
        String viewName = productController.showformProducts(model);

        assertEquals("product/add-product", viewName);
        verify(model).addAttribute(eq("category"), any());
        verify(model).addAttribute(eq("supplier"), any());
    }

    @Test
    void saveProductAndImage_ValidProduct_RedirectsToForm() throws Exception {
        // Configurar el mock para que file.getBytes() no falle
        when(file.getBytes()).thenReturn("dummy image data".getBytes());

        // Configurar Cloudinary para que devuelva un mapa con la URL
        Map<String, String> uploadResultMap = new HashMap<>();
        uploadResultMap.put("url", "https://fake-url.com/image.jpg");
        when(cloudinaryConfig.upload(any(byte[].class), any(Map.class))).thenReturn(uploadResultMap);

        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(productService.saveProduct(any(Product.class))).thenReturn(testProduct);
        when(categoryService.getById(anyLong())).thenReturn(testCategory);
        when(supplierService.getById(anyLong())).thenReturn(testSupplier);

        // Ejecutar
        String viewName = productController.saveProductAndImage(testProduct, bindingResult, model, file);

        // Verificar
        assertEquals("redirect:/product/registroProduct", viewName);
        verify(productService, times(1)).saveProduct(any(Product.class)); // times(1) para claridad
        verify(cloudinaryConfig, times(1)).upload(any(byte[].class), any(Map.class)); // times(1) para claridad
        verify(model).addAttribute(eq("successMessage"), anyString()); // Verificar el mensaje de éxito
    }

    @Test
    void saveProductAndImage_InvalidImage_ReturnsError() throws Exception {
        // Configurar
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(categoryService.listCategory()).thenReturn(List.of(testCategory));
        when(supplierService.listSuppliers()).thenReturn(List.of(testSupplier));

        // Ejecutar
        String viewName = productController.saveProductAndImage(testProduct, bindingResult, model, file);

        // Verificar
        assertEquals("product/add-product", viewName);
        verify(model).addAttribute(eq("error"), eq("Solo se permiten archivos de imagen JPEG."));
        verify(productService, never()).saveProduct(any());
        verify(cloudinaryConfig, never()).upload(any(), any());
    }

    @Test
    void editProduct_ProductExists_ReturnsEditView() {
        when(productService.getByIdProduct(anyLong())).thenReturn(testProduct);
        when(categoryService.listCategory()).thenReturn(List.of(testCategory));
        when(supplierService.listSuppliers()).thenReturn(List.of(testSupplier));

        String viewName = productController.showUpdateProduct(1L, model);

        assertEquals("product/update-product", viewName);
        verify(model).addAttribute(eq("product"), eq(testProduct));
        verify(model).addAttribute(eq("categories"), any(List.class));
        verify(model).addAttribute(eq("suppliers"), any(List.class));

    }

    @Test
    void saveProductAndImage_CloudinaryError_ReturnsError() throws Exception {
        // Configurar
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getBytes()).thenReturn("dummy image data".getBytes());

        when(categoryService.listCategory()).thenReturn(List.of(testCategory));
        when(supplierService.listSuppliers()).thenReturn(List.of(testSupplier));

        when(cloudinaryConfig.upload(any(), any())).thenThrow(new RuntimeException("Cloudinary error"));

        // Ejecutar
        String viewName = productController.saveProductAndImage(testProduct, bindingResult, model, file);

        // Verificar
        assertEquals("product/add-product", viewName);
        verify(model).addAttribute(eq("error"), any(String.class));
        verify(productService, never()).saveProduct(any()); // Asegurarse de que no se guarde si hay error de Cloudinary
    }

    @Test
    void updateProduct_ValidUpdate_RedirectsToList() throws Exception {
        when(productService.getByIdProduct(anyLong())).thenReturn(testProduct);
        when(file.isEmpty()).thenReturn(true);
        when(productService.saveProduct(any(Product.class))).thenReturn(testProduct);

        when(categoryService.getById(anyLong())).thenReturn(testCategory);
        when(supplierService.getById(anyLong())).thenReturn(testSupplier);

        String viewName = productController.updateProduct(1L, testProduct, model, file);

        assertEquals("redirect:/product/listProducts", viewName);
        verify(productService).saveProduct(testProduct);
        verify(model).addAttribute(eq("successMessage"), anyString()); // Verificar el mensaje de éxito
        verify(cloudinaryConfig, never()).upload(any(), any()); // No debe llamar a Cloudinary si el archivo está vacío
    }

    @Test
    void deleteProduct_ValidId_RedirectsToList() {

        when(productService.getByIdProduct(1L)).thenReturn(testProduct);

        doNothing().when(productService).deleteProduct(anyLong());

        String viewName = productController.deleteProduct(1L, model);

        assertEquals("redirect:/product/listProducts", viewName);
        verify(productService).deleteProduct(1L);
        verify(model).addAttribute(eq("product"), any(List.class));
    }

    // Test para buscar productos por nombre
    @Test
    void searchProducts_WithMatches_ReturnsMatchingList() {
        String searchTerm = "Lapt";
        List<Product> matchedProducts = List.of(testProduct);

        when(productService.searchProductsByName(searchTerm)).thenReturn(matchedProducts);

        String viewName = productController.searchProducts(searchTerm, model);

        assertEquals("product/listProduct", viewName);
        verify(model).addAttribute("ListProducts", matchedProducts);
        verify(model, never()).addAttribute(eq("noResults"), anyString());
    }

    @Test
    void searchProducts_NoMatches_ShowsNoResultsMessage() {
        String searchTerm = "NonExisting";
        List<Product> emptyList = List.of();

        when(productService.searchProductsByName(searchTerm)).thenReturn(emptyList);

        String viewName = productController.searchProducts(searchTerm, model);

        assertEquals("product/listProduct", viewName);
        verify(model).addAttribute("ListProducts", emptyList);
        verify(model).addAttribute("noResults", "No se encontraron productos");
    }

    // Test para filtrar productos por categoría
    @Test
    void filterProductsByCategory_WithResults_DisplaysFilteredProducts() {
        String categoryName = "Electronics";
        List<Product> filtered = List.of(testProduct);

        when(productService.filterProductsByCategory(categoryName)).thenReturn(filtered);
        when(categoryService.listCategory()).thenReturn(List.of(testCategory));

        String viewName = productController.filterProductsByCategory(categoryName, model);

        assertEquals("/exploreProductsCategories", viewName);
        verify(model).addAttribute("products", filtered);
        verify(model).addAttribute("categories", List.of(testCategory));
        verify(model, never()).addAttribute(eq("noProductsMessage"), anyString());
    }

    @Test
    void filterProductsByCategory_NoResults_DisplaysNoProductsMessage() {
        String categoryName = "EmptyCategory";
        List<Product> emptyList = List.of();

        when(productService.filterProductsByCategory(categoryName)).thenReturn(emptyList);
        when(categoryService.listCategory()).thenReturn(List.of(testCategory));

        String viewName = productController.filterProductsByCategory(categoryName, model);

        assertEquals("/exploreProductsCategories", viewName);
        verify(model).addAttribute("noProductsMessage", "No hay productos disponibles en este momento.");
        verify(model).addAttribute("categories", List.of(testCategory));
        verify(model, never()).addAttribute(eq("products"), any());
    }

}
