package com.arpo.controller;

import com.arpo.models.Rol;
import com.arpo.models.User;
import com.arpo.service.RolService;
import com.arpo.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RolService rolService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private Rol testRol;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testRol = new Rol();
        testRol.setId_Rol(1);
        testRol.setName_rol("Admin");

        testUser = new User();
        testUser.setIdUser(12345L);
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setAge(30);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setAddress("123 Test St");
        testUser.setPhoneNumber("1234567890");
        testUser.setIdRol(testRol);

        when(rolService.getRolById(anyInt())).thenReturn(testRol);
    }

    @Test
    void listaUsuarios() {
        List<User> userList = List.of(testUser);
        when(userService.listUser()).thenReturn(userList);

        String viewName = userController.listaUsuarios(model);

        assertEquals("usuario/listar-usuarios", viewName);
        verify(model).addAttribute("ListaDeUsuarios", userList);
    }

    @Test
    void guardarUsuario_ValidUser_RedirectsToList() {
        when(userService.alReadyExist(testUser.getIdUser())).thenReturn(false);
        when(userService.isEmailDuplicated(testUser.getEmail())).thenReturn(false);
        when(userService.save(testUser)).thenReturn(testUser);

        String viewName = userController.guardarUsuario(testUser, model);

        assertEquals("redirect:/user/listado-usuarios", viewName);
        verify(userService).save(testUser);
    }

 

  
    @Test
    void deleteEmpleado() {
        when(userService.getById(testUser.getIdUser())).thenReturn(testUser);
        doNothing().when(userService).delete(testUser.getIdUser());

        String viewName = userController.deleteEmpleado(testUser.getIdUser(), model);

        assertEquals("redirect:/user/listado-usuarios", viewName);
        verify(userService).delete(testUser.getIdUser());
    }
}
