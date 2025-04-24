package com.arpo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arpo.models.CategoryProduct;
import com.arpo.service.CategoryProductService;

@Controller
@RequestMapping("/category")
public class CategoryProductController {
	
	@Autowired
	private CategoryProductService categoryService;
	
	@GetMapping("/registroCategory")
		public String mostrarFormularioRegistro(Model model) {
		model.addAttribute("categoryProduct",new CategoryProduct());
		return "category/add-category";
	}
	
	@GetMapping("/listCategory")
	public String listaCategory(Model model) {
		model.addAttribute("ListaDeCategorias",categoryService.listCategory());
		return "category/listar-category";
	}
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute CategoryProduct categoryProduct, Model model) {
		categoryService.save(categoryProduct);
		return "redirect:/category/registroCategory";
	}
	
	@GetMapping("/admin/updateCategory/{idCategoryProduct}")
	public String showUpdateCategory(@PathVariable("idCategoryProduct") Long idCategoryProduct,Model model) {
		CategoryProduct category = categoryService.getById(idCategoryProduct);
		model.addAttribute("categoryProduct", category);
		return "category/update-category";
	}
	
	
	@PostMapping("/admin/updateCategory/{idCategoryProduct}")
	public String updateCategory(@PathVariable("idCategoryProduct") Long idCategoryProduct,CategoryProduct category,
			BindingResult result,Model model) {
		if(result.hasErrors()) {
			return "category/update-category";
		}
		
		CategoryProduct alreadyExist = categoryService.getById(idCategoryProduct);
		if(alreadyExist != null) {
			alreadyExist.setNameCategory(category.getNameCategory());
			
			categoryService.save(alreadyExist);
			
			model.addAttribute("successMessage", "La categoria ha sido modificada");
			return "redirect:/category/listCategory";
			
		}
		return null;
	}
	
	@GetMapping("/admin/deleteCategory/{idCategoryProduct}")
	public String deletecategory(@PathVariable("idCategoryProduct")Long idCategoryProduct, Model model) {
		CategoryProduct category = categoryService.getById(idCategoryProduct);
		categoryService.delete(category.getIdCategoryProduct());
		model.addAttribute("category", categoryService.listCategory());
		return "redirect:/category/listCategory";
	}
	
	
	

}
