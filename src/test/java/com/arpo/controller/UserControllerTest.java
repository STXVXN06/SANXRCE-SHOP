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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    }



    @Test
    void listaUsuarios_ReturnsListUsersView() {
        List<User> userList = new ArrayList<>();
        userList.add(testUser);
        when(userService.listUser()).thenReturn(userList);

        String viewName = userController.listaUsuarios(model);

        assertEquals("usuario/listar-usuarios", viewName);
        verify(model).addAttribute(eq("ListaDeUsuarios"), eq(userList));
    }

    @Test
    void guardarUsuario_ValidUser_RedirectsToList() {
        when(userService.alReadyExist(anyLong())).thenReturn(false);
        when(userService.isEmailDuplicated(anyString())).thenReturn(false);
        when(rolService.getRolById(anyInt())).thenReturn(testRol);
        when(userService.save(any(User.class))).thenReturn(testUser);

        String viewName = userController.guardarUsuario(testUser, model);

        assertEquals("redirect:/user/listado-usuarios", viewName);
        verify(userService).save(testUser);
    }

 //   @Test
 //   void guardarUsuario_DuplicateId_ReturnsErrorView() {
   //     when(userService.alReadyExist(anyLong())).thenReturn(true);

     //   String viewName = userController.guardarUsuario(testUser, model);

       // assertEquals("/error", viewName);
        //verify(model).addAttribute(eq("error"), eq("El ID de usuario ya existe"));
    //}

  //  @Test
    //void guardarUsuario_DuplicateEmail_ReturnsErrorView() {
      //  when(userService.alReadyExist(anyLong())).thenReturn(false);
      //  when(userService.isEmailDuplicated(anyString())).thenReturn(true);

       // String viewName = userController.guardarUsuario(testUser, model);

        //assertEquals("/error", viewName);
        //verify(model).addAttribute(eq("error"), eq("Ya existe una cuenta asociada con ese email."));
    //}

   

    @Test
    void updateUser_ValidUser_RedirectsToList() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getById(anyLong())).thenReturn(testUser);
        when(userService.save(any(User.class))).thenReturn(testUser);

        String viewName = userController.updateUser(testUser.getIdUser(), testUser, bindingResult, model);

        assertEquals("redirect:/user/listado-usuarios", viewName);
        verify(userService).save(testUser);
        verify(model).addAttribute(eq("successMessage"), anyString());
    }

    @Test
    void deleteEmpleado_DeletesUserAndRedirectsToList() {
        when(userService.getById(anyLong())).thenReturn(testUser);
        doNothing().when(userService).delete(anyLong());
        when(userService.listUser()).thenReturn(new ArrayList<>()); // Simulate list after deletion

        String viewName = userController.deleteEmpleado(testUser.getIdUser(), model);

        assertEquals("redirect:/user/listado-usuarios", viewName);
        verify(userService).delete(testUser.getIdUser());
    }
}