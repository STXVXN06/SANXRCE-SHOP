package com.arpo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arpo.models.Order;
import com.arpo.models.User;
import com.arpo.service.OrderService;
import com.arpo.service.UserService;
import com.arpo.utils.PDF;
import com.lowagie.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	public OrderService orderService;
	
	@Autowired
	public UserService userService;
	
	
	@GetMapping("/showOrders")
	public String obtenerCompras(Model model, HttpSession session) {
		model.addAttribute("sesion", session.getAttribute("userId"));
		
		User usuario= userService.findById(Long.parseLong(session.getAttribute("userId").toString()) ).get();
		List<Order> ordenes= orderService.findByUsuario(usuario);
		
		model.addAttribute("orders", ordenes);
		
		return "client/clientListOrder";
	}
	
	
	@GetMapping("/export/pdf/{id}")
	public void exportToPDF(@PathVariable int id, HttpServletResponse response) throws DocumentException, IOException{
	        response.setContentType("application/pdf");
	        Optional<Order> orderOptional = orderService.findById(id);

	        if (orderOptional.isPresent()) {
	            Order order = orderOptional.get();
	            System.out.print("si entro");
	            String headerKey = "Content-Disposition";
	            String headerValue = "attachment; filename=factura_"+ order.getUser().getName()+
	            		"_"+id+".pdf";
	            response.setHeader(headerKey, headerValue);

	            PDF exporter = new PDF(order);
	            exporter.export(response);
	        } else {
	            response.getWriter().println("La orden no fue encontrada.");
	            
	        }
	}
	
	@GetMapping("/editOrder/{id}")
	public String ShowEditOrder(@PathVariable Integer id, Model model) {
		Optional<Order> order =  orderService.findById(id);
		model.addAttribute("order", order.orElse(null));
		return "order/editStatusOrder";
	}
	
	@PostMapping("/guardar-edicion/{idOrder}")
	public String editStatusOrder(@PathVariable("idOrder") Integer idOrder, Order order, Model model) {
	    Optional<Order> alreadyExist = orderService.findById(idOrder);
	    Order orderExist = alreadyExist.get();
	    if (alreadyExist.isPresent()) {
	        
	        orderExist.setStatus(order.getStatus());
	        orderService.save(orderExist);
	        return "redirect:/order/listOrders";
	    }
		return null;
	}

	
	@GetMapping("/listOrders")
	public String obtenerCompras(Model model) {
	    List<User> usuarios = userService.listUser();

	    List<Order> todasLasOrdenes = new ArrayList<>();
	    Order order = new Order();
	    for (User usuario : usuarios) {
	        List<Order> ordenesUsuario = orderService.findByUsuario(usuario);
	        todasLasOrdenes.addAll(ordenesUsuario);
	    }
	    model.addAttribute("order", order);
	    model.addAttribute("orders", todasLasOrdenes);

	    return "order/listOrder";
	}

	 @ModelAttribute("statusOptions")
	    public List<String> getStatusOptions() {
	        List<String> statusOptions = Arrays.asList("Procesado", "Enviado", "Recibido");
	        return statusOptions;
	    }
	 
	@PostMapping("/filtrarOrdenes")
	public String filtrarOrdenes(@RequestParam String status, Model model) {
	    if (status == null || status.isEmpty() || status.equals("Seleccionar...")) {
	        model.addAttribute("orders", orderService.findAll());
	    } else {
	        Iterable<Order> filteredOrders = orderService.getByStatus(status);
	        model.addAttribute("orders", filteredOrders);
	    }
	    model.addAttribute("statusOptions", getStatusOptions());


	    return "order/listOrder"; 
	}

	
	
	
	

}

