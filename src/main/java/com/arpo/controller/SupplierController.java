package com.arpo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arpo.models.Product;
import com.arpo.models.Supplier;
import com.arpo.service.ProductService;
import com.arpo.service.SupplierService;

@Controller
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @GetMapping("/registroSupplier")
    public String showformSupplier(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "supplier/add-supplier";
    }

    @GetMapping("/listSupplier")
    public String listaSupplier(Model model) {
        model.addAttribute("ListSupplier", supplierService.listSuppliers());
        return "supplier/listar-supplier";
    }

    @PostMapping("/saveSupplier")
    public String saveSupplier(@ModelAttribute Supplier supplier, Model model) {

        if (supplier.getIdSupplier() != null && supplierService.alReadyExist(supplier.getIdSupplier())) {
            model.addAttribute("error", "El proveedor ya existe.");
            return "supplier/add-supplier";
        }

        supplierService.save(supplier);
        return "redirect:/supplier/registroSupplier";
    }

    @GetMapping("/admin/updateSupplier/{idSupplier}")
    public String showUpdateSupplier(@PathVariable("idSupplier") Long idSupplier, Model model) {
        Supplier supplier = supplierService.getById(idSupplier);
        model.addAttribute("supplier", supplier);
        return "supplier/update-supplier";
    }

    @PostMapping("/admin/updateSupplier/{idSupplier}")
    public String updateSupplier(@PathVariable("idSupplier") Long idSupplier, Supplier supplier,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("supplier", supplier);
            return "supplier/update-supplier";
        }

        Supplier alreadyExist = supplierService.getById(idSupplier);
        if (alreadyExist != null) {
            alreadyExist.setNameSupplier(supplier.getNameSupplier());
            alreadyExist.setAddress(supplier.getAddress());
            alreadyExist.setPhoneNumber(supplier.getPhoneNumber());

            supplierService.save(alreadyExist);

            model.addAttribute("successMessage", "El proveedor ha sido modificado.");
            return "redirect:/supplier/listSupplier";
        }
        model.addAttribute("errorMessage", "Proveedor no encontrado para actualizar.");
        return "redirect:/supplier/listSupplier";
    }

    @GetMapping("/admin/deleteSupplier/{idSupplier}")
    public String deleteSupplier(@PathVariable("idSupplier") Long idSupplier, Model model) {
        Supplier supplier = supplierService.getById(idSupplier);
        if (supplier == null) {
            model.addAttribute("errorMessage", "Proveedor no encontrado.");
            return "redirect:/supplier/listSupplier";
        }
        List<Product> productos = productService.getBySupplier(supplier.getIdSupplier());

        if (!productos.isEmpty()) {
            model.addAttribute("errorIdDuplicado", true);
            model.addAttribute("errorMessage", "No se puede eliminar el proveedor porque tiene productos asociados.");
            return "redirect:/supplier/listSupplier";
        }

        supplierService.delete(supplier.getIdSupplier());
        model.addAttribute("successMessage", "Proveedor eliminado exitosamente.");
        return "redirect:/supplier/listSupplier";
    }

}
