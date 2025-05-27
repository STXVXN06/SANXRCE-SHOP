package com.arpo.controller;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.arpo.models.Product;
import com.arpo.models.Supplier;
import com.arpo.service.ProductService;
import com.arpo.service.SupplierService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SupplierControllerTest {

    @Mock
    private SupplierService supplierService;

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private SupplierController supplierController;

    private Supplier supplier;
    private Product product; // producto de prueba

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        supplier = new Supplier();
        supplier.setIdSupplier(1L);
        supplier.setNameSupplier("Proveedor Ejemplo");
        supplier.setPhoneNumber("1234567890");
        supplier.setAddress("Calle Falsa 123");

        product = new Product();
        product.setIdProduct(1L);
        product.setNameProduct("Producto del proveedor");
        product.setIdSupplier(supplier);
    }

    @Test
    void showAddSupplierForm_ReturnsCorrectView() {
        String view = supplierController.showformSupplier(model);
        assertEquals("supplier/add-supplier", view);
        verify(model).addAttribute(eq("supplier"), any(Supplier.class));
    }

    @Test
    void listSuppliers_ReturnsCorrectView() {
        when(supplierService.listSuppliers()).thenReturn(List.of(supplier));

        String view = supplierController.listaSupplier(model);
        assertEquals("supplier/listar-supplier", view);
        verify(model).addAttribute("ListSupplier", List.of(supplier));
    }

    @Test
    void saveSupplier_NewSupplier_Redirects() {
        when(supplierService.alReadyExist(anyLong())).thenReturn(false);
        when(supplierService.alReadyExist(null)).thenReturn(false);
        String view = supplierController.saveSupplier(supplier, model);

        assertEquals("redirect:/supplier/registroSupplier", view);
        verify(supplierService).save(supplier);
    }

    @Test
    void saveSupplier_ExistingSupplier_ReturnsError() {
        supplier.setIdSupplier(1L);
        when(supplierService.alReadyExist(supplier.getIdSupplier())).thenReturn(true);

        String view = supplierController.saveSupplier(supplier, model);

        assertEquals("supplier/add-supplier", view);
        verify(model).addAttribute(eq("error"), eq("El proveedor ya existe."));
        verify(supplierService, never()).save(any());
    }

    @Test
    void showUpdateSupplier_ReturnsCorrectViewWithSupplier() {
        when(supplierService.getById(1L)).thenReturn(supplier);

        String view = supplierController.showUpdateSupplier(1L, model);
        assertEquals("supplier/update-supplier", view);
        verify(model).addAttribute("supplier", supplier);
    }

    @Test
    void updateSupplier_ValidChanges_RedirectsToList() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(supplierService.getById(1L)).thenReturn(supplier);
        when(supplierService.save(any(Supplier.class))).thenReturn(supplier); // Mockear el guardado

        Supplier updated = new Supplier();
        updated.setIdSupplier(1L); // ID presente para la actualización
        updated.setNameSupplier("Nuevo");
        updated.setAddress("Otra calle");
        updated.setPhoneNumber("555555");

        String view = supplierController.updateSupplier(1L, updated, bindingResult, model);

        assertEquals("redirect:/supplier/listSupplier", view);
        verify(supplierService).save(any(Supplier.class)); // Verifica que se llamó a save con cualquier Supplier
        verify(model).addAttribute(eq("successMessage"), eq("El proveedor ha sido modificado."));
    }

    @Test
    void updateSupplier_WithErrors_ReturnsSameForm() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(supplierService.getById(anyLong())).thenReturn(supplier);

        String view = supplierController.updateSupplier(1L, supplier, bindingResult, model);
        assertEquals("supplier/update-supplier", view);
        verify(model).addAttribute(eq("supplier"), eq(supplier));
    }

    @Test
    void deleteSupplier_NoProductsAssociated_RedirectsToList() {
        when(supplierService.getById(1L)).thenReturn(supplier);
        when(productService.getBySupplier(1L)).thenReturn(List.of()); //  No Hay productos asociados
        String view = supplierController.deleteSupplier(1L, model);

        assertEquals("redirect:/supplier/listSupplier", view);
        verify(supplierService).delete(1L);
        verify(model).addAttribute(eq("successMessage"), any(String.class));
        verify(supplierService, never()).listSuppliers();
    }

    @Test
    void deleteSupplier_HasProductsAssociated_ShowsError() {
        when(supplierService.getById(1L)).thenReturn(supplier);
        when(productService.getBySupplier(1L)).thenReturn(List.of(product)); // Hay productos asociados
        String view = supplierController.deleteSupplier(1L, model);

        assertEquals("redirect:/supplier/listSupplier", view);
        verify(model).addAttribute(eq("errorIdDuplicado"), eq(true));
        verify(model).addAttribute(eq("errorMessage"), any(String.class));
        verify(supplierService, never()).delete(anyLong());
        verify(supplierService, never()).listSuppliers();
    }
}
