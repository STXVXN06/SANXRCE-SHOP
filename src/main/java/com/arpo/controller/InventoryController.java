package com.arpo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arpo.models.CategoryProduct;
import com.arpo.service.CategoryProductService;
import com.arpo.service.ProductService;

@Controller
@RequestMapping("/inventory")
public class InventoryController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryProductService categoryService;
	
	@GetMapping("/listProducts")
	public String listProducts(Model model) {
		List<CategoryProduct> categories = categoryService.listCategory();
		model.addAttribute("inventory",categories);
		model.addAttribute("ListProducts",productService.listProduct());
		return "inventory/list-inventory";
	}
	
	
	@PostMapping("/filtrarProductos")
	public String filtrarProductos(@RequestParam Long idCategory, Model model) {
		if (idCategory == null || idCategory <= 0) {
			model.addAttribute("ListProducts", productService.listProduct());
		} else {
			model.addAttribute("ListProducts", productService.getByCategory(idCategory));
		}
		List<CategoryProduct> categories = categoryService.listCategory();
		model.addAttribute("inventory", categories);
		return "inventory/list-inventory";
	}
	
	

}
