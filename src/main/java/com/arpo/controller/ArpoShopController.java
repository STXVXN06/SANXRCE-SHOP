package com.arpo.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arpo.models.CategoryProduct;
import com.arpo.models.Product;
import com.arpo.models.Rol;
import com.arpo.models.User;
import com.arpo.service.CategoryProductService;
import com.arpo.service.ProductService;
import com.arpo.service.RolService;
import com.arpo.service.UserService;
import com.arpo.singleton.Singleton;

import jakarta.servlet.http.HttpSession;

@Controller
public class ArpoShopController {

	@Autowired
    private Singleton userSingleton;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryProductService categoryService;
	
	@Autowired
	private UserService userService;
	
	 @Autowired
	 private RolService rolService;
	
	
	@GetMapping({"/"})
	public String ShowLogin() {
		return "login";
	}
    @GetMapping({"/login"})
	public String ShowLogin2() {
		return "login";
	}
	
	@GetMapping({"/signIn"})
	public String signIn(Model model) {
		model.addAttribute("user", new User());
		return "signIn";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
	    session.invalidate();
	    return "redirect:/";
	}
	

    @PostMapping("/userProfile")
    public String login(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        Optional<User> user = userSingleton.login(email, password);

        if (user.isPresent()) {
            Rol userRole = user.get().getIdRol();
            model.addAttribute("user", user.get());
            ArrayList<Product> products = (ArrayList<Product>) productService.getActiveProducts();
    	    ArrayList<CategoryProduct> categories = (ArrayList<CategoryProduct>) categoryService.listCategory();
    	    if (products.isEmpty()) {
    	        model.addAttribute("noProductsMessage", "No hay productos disponibles en este momento.");
    	    } else {
    	        model.addAttribute("products", products);
    	    }
    	    session.setAttribute("userId", user.get().getIdUser());
    	    model.addAttribute("categories", categories);
            if (userRole.getName_rol().equals("Admin")|| userRole.getName_rol().equals("Empleado")) {
                return "exploreProducts"; 
            } else if (userRole.getName_rol().equals("Cliente")) {
            	return "client/clientTemplate"; 
            }
        }
        model.addAttribute("errorMessage", "Credenciales incorrectas. Intente nuevamente.");
        return "login"; 
    }



    @PostMapping("/user-registration")
    private String userRegistration(@ModelAttribute("user") @Validated User user, Model model) {
        boolean usuarioId = userService.alReadyExist(user.getIdUser());
        boolean emailUsed = userService.isEmailDuplicated(user.getEmail());

        if (usuarioId) {
            model.addAttribute("error", "El ID de usuario ya existe");
            return "/error";
        } else if (emailUsed) {
        	model.addAttribute("error", "Ya existe una cuenta asociada con ese email.");
        	return "/error";
        } else {
            int idRol = 1;
            Rol rolSeleccionado = rolService.getRolById(idRol);
            user.setIdRol(rolSeleccionado);
            userService.save(user);
            return "redirect:/";
        }
    }



	
}
