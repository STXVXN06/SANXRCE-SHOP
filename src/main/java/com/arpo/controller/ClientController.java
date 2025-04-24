package com.arpo.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arpo.models.CategoryProduct;
import com.arpo.models.Order;
import com.arpo.models.Product;
import com.arpo.models.User;
import com.arpo.service.CategoryProductService;
import com.arpo.service.OrderService;
import com.arpo.service.ProductService;
import com.arpo.singleton.Singleton;

import jakarta.servlet.http.HttpSession;

@Controller 
@RequestMapping("client")
public class ClientController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryProductService categoryService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private Singleton singleton;
	
	@GetMapping("/available-products")
	public String getActiveProduct(Model model,HttpSession session) {
	    ArrayList<Product> products = (ArrayList<Product>) productService.getActiveProducts();
	    ArrayList<CategoryProduct> categories = (ArrayList<CategoryProduct>) categoryService.listCategory();
	    Long userId = (Long) session.getAttribute("userId");
	    
	    if (userId != null) {
	    	 Optional<User> user = singleton.getUser(userId);
	    	 if (user.isPresent()) {
	    		 model.addAttribute("user", user.get());
			}
		}
	    if (products.isEmpty()) {
	        model.addAttribute("noProductsMessage", "No hay productos disponibles en este momento.");
	    } else {
	        model.addAttribute("products", products);
	    }
	    model.addAttribute("categories", categories);
	    return "/client/clientTemplate";
	}

	@GetMapping("/categoryproduct/{nameCategory}")
	public String filterProductsByCategory(@PathVariable("nameCategory") String nameCategory, Model model ,HttpSession session) {
	    ArrayList<Product> filteredProducts = (ArrayList<Product>) productService.filterProductsByCategory(nameCategory);
	    Long userId = (Long) session.getAttribute("userId");
	    
	    if (userId != null) {
	    	 Optional<User> user = singleton.getUser(userId);
	    	 if (user.isPresent()) {
	    		 model.addAttribute("user", user.get());
			}
		}
	    if (filteredProducts.isEmpty()) {
	        model.addAttribute("noProductsMessage", "No hay productos disponibles en este momento.");
	    } else {
	        model.addAttribute("products", filteredProducts);
	    }
	    model.addAttribute("categories", categoryService.listCategory());

	    return "/client/clientCategoriesProduct";
	}

	@GetMapping("/detailproduct/{idProduct}")
   	public String viewDetail(@PathVariable("idProduct") Long idProduct, Model model ,HttpSession session) {
   		Product product = productService.getByIdProduct(idProduct);
   		Long userId = (Long) session.getAttribute("userId");
	    
	    if (userId != null) {
	    	 Optional<User> user = singleton.getUser(userId);
	    	 if (user.isPresent()) {
	    		 model.addAttribute("user", user.get());
			}
		}
	    if(product.getStock() == 0) {
	    	model.addAttribute("message", "El producto no se encuentra disponible");
	    }
   		model.addAttribute("product", product);
   		return "client/clientDetailProduct";
   	}
	
	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model) {
		Optional<Order> orden=orderService.findById(id);
		
		model.addAttribute("detalles", orden.get().getDetalle());
		model.addAttribute("sesion", session.getAttribute("userId"));
		return "usuario/detallecompra";
	}
	
	
}
