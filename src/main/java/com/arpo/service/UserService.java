package com.arpo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arpo.models.User;
import com.arpo.repository.IUserRepository;

@Service
public class UserService {
	
	@Autowired
	private IUserRepository userRepository;
	
	public List<User> listUser(){
		return userRepository.findAll();
	}
	
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
	
	public boolean alReadyExist(Long id) {
        Optional<User> usuarioExistente = userRepository.findById(id);
        return usuarioExistente.isPresent();
    }
	
	public boolean isEmailDuplicated(String email) {
	    List<User> users = userRepository.findAll(); 

	    for (User user : users) {
	        if (user.getEmail().equalsIgnoreCase(email)) {
	            return true; 
	        }
	    }

	    return false; 
	}

	public Optional<User> findByEmail(String email){
		return userRepository.findByEmail(email);
	}

	
	 public User getById(Long id) {
		 return userRepository.findById(id)
		            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
	 }

	 public User save(User user) {
	    return userRepository.save(user);
	 }

	 public void delete(Long id) {
	     userRepository.deleteById(id);
	 }
}
