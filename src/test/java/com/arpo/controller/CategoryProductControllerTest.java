package com.arpo.controller;

import com.arpo.models.CategoryProduct;
import com.arpo.service.CategoryProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryProductControllerTest {

    @Mock
    private CategoryProductService categoryService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private CategoryProductController categoryProductController;

    private CategoryProduct testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = new CategoryProduct();
        testCategory.setIdCategoryProduct(1L);
        testCategory.setNameCategory("Electronics");
    }



    @Test
    void listaCategory_ReturnsListCategoryView() {
        List<CategoryProduct> categoryList = new ArrayList<>();
        categoryList.add(testCategory);
        when(categoryService.listCategory()).thenReturn(categoryList);

        String viewName = categoryProductController.listaCategory(model);

        assertEquals("category/listar-category", viewName);
        verify(model).addAttribute(eq("ListaDeCategorias"), eq(categoryList));
    }
    
   
  



    @Test
    void saveCategory_ValidCategory_RedirectsToRegistro() {
        when(categoryService.save(any(CategoryProduct.class))).thenReturn(testCategory);

        String viewName = categoryProductController.saveCategory(testCategory, model);

        assertEquals("redirect:/category/registroCategory", viewName);
        verify(categoryService).save(testCategory);
    }

    @Test
    void showUpdateCategory_CategoryFound_ReturnsUpdateCategoryView() {
        when(categoryService.getById(anyLong())).thenReturn(testCategory);

        String viewName = categoryProductController.showUpdateCategory(testCategory.getIdCategoryProduct(), model);

        assertEquals("category/update-category", viewName);
        verify(model).addAttribute(eq("categoryProduct"), eq(testCategory));
    }

    @Test
    void updateCategory_ValidCategory_RedirectsToList() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(categoryService.getById(anyLong())).thenReturn(testCategory);
        when(categoryService.save(any(CategoryProduct.class))).thenReturn(testCategory);

        String viewName = categoryProductController.updateCategory(testCategory.getIdCategoryProduct(), testCategory, bindingResult, model);

        assertEquals("redirect:/category/listCategory", viewName);
        verify(categoryService).save(testCategory);
        verify(model).addAttribute(eq("successMessage"), anyString());
    }

    @Test
    void deletecategory_DeletesCategoryAndRedirectsToList() {
        when(categoryService.getById(anyLong())).thenReturn(testCategory);
        doNothing().when(categoryService).delete(anyLong());
        when(categoryService.listCategory()).thenReturn(new ArrayList<>()); // Simulate list after deletion

        String viewName = categoryProductController.deletecategory(testCategory.getIdCategoryProduct(), model);

        assertEquals("redirect:/category/listCategory", viewName);
        verify(categoryService).delete(testCategory.getIdCategoryProduct());
    }
}