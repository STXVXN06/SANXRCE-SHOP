package com.arpo.singleton;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arpo.models.User;
import com.arpo.service.UserService;

@Component
public class Singleton {
	
    
    
    @Autowired
    private UserService userService;

	public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.orElseThrow();
            if (password.equalsIgnoreCase( user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public Optional<User> getUser(Long id) {
        return userService.findById(id);
    }

    public List<User> getAllUsers() {
        return userService.listUser();
    }

}
