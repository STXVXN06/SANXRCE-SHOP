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

import com.arpo.models.Rol;
import com.arpo.models.User;
import com.arpo.service.RolService;
import com.arpo.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	 @Autowired
	 private UserService userService;
	 
	 @Autowired
	 private RolService rolService;
	    
	 @GetMapping("/registro")
	    public String mostrarFormularioRegistro(Model model) {
	        List<Rol> roles = rolService.getRoleList();
	        model.addAttribute("roles", roles);
	        model.addAttribute("usuario", new User());
	        return "usuario/add-user";
	    }
	    
	    @GetMapping("/listado-usuarios")
	    public String listaUsuarios(Model model) {
	        model.addAttribute("ListaDeUsuarios", userService.listUser());
	        return "usuario/listar-usuarios";
	    }

	    @PostMapping("/guardarUsuarios")
	    public String guardarUsuario(@ModelAttribute User usuario, Model model) {
	        boolean usuarioId = userService.alReadyExist(usuario.getIdUser());
	        boolean emailUsed = userService.isEmailDuplicated(usuario.getEmail());
	        if(usuarioId ) {
	            model.addAttribute("errorIdDuplicado", true);
	           
	        }else if(emailUsed) {
	        	 model.addAttribute("errorEmailDuplicado",true);
	        }else {
	            int idRol = usuario.getIdRol().getId_Rol();
	            Rol rolSeleccionado = rolService.getRolById(idRol);
	            usuario.setIdRol(rolSeleccionado);
	            userService.save(usuario);
	            return "redirect:/user/listado-usuarios";
	        }
	        
	        List<Rol> roles = rolService.getRoleList();
	        model.addAttribute("roles", roles);
	        model.addAttribute("usuario", usuario);
	        return "usuario/add-user";
	    }

	    @GetMapping("/admin/editUser/{idUser}")
		public String showUpdateForm(@PathVariable("idUser") Long idUser, Model model) {
			User user = userService.getById(idUser);
			model.addAttribute("usuario", user);
			return "usuario/update-user";
	    }
		
	    
	    @PostMapping("/admin/updateUser/{idUser}")
		public String updateUser(@PathVariable("idUser") Long id, User user, BindingResult result, Model model) {
	    	if (result.hasErrors()) {
	            return "usuario/update-user";
	    	}

	    	User alreadyExist = userService.getById(id);
	        if (alreadyExist != null) {
	        	alreadyExist.setName(user.getName());
	        	alreadyExist.setSurname(user.getSurname());
	        	alreadyExist.setAddress(user.getAddress());
	        	alreadyExist.setAge(user.getAge());
	        	alreadyExist.setEmail(user.getEmail());
	        	alreadyExist.setPhoneNumber(user.getPhoneNumber());
	        	alreadyExist.setPassword(user.getPassword());

	            userService.save(alreadyExist);
	            
	            model.addAttribute("successMessage", "El usuario ha sido modificado con éxito.");
	            return "redirect:/user/listado-usuarios";
	        }
			return null;
		}
	    
	    
	    @GetMapping("/admin/deleteUser/{idUser}")
	    public String deleteEmpleado(@PathVariable("idUser") Long idUser, Model model) {
	 	        User user = userService.getById(idUser);
	 	        userService.delete(user.getIdUser());
	 	        model.addAttribute("usuario", userService.listUser());
	 	        return "redirect:/user/listado-usuarios";
	    }

   
}