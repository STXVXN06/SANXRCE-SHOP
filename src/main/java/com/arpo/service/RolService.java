package com.arpo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arpo.models.Rol;
import com.arpo.repository.IRolRepository;

@Service
public class RolService {

	@Autowired
	private IRolRepository rolRepository;
	
	public List<Rol> getRoleList() {
	     return rolRepository.findAll(); 
	}
	
	public Rol getRolById(int id) {
		return rolRepository.findById(id).orElse(null);
	}
	
	
}
