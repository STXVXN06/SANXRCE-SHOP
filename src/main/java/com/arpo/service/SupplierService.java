package com.arpo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arpo.models.Supplier;
import com.arpo.repository.ISupplierRepository;

@Service
public class SupplierService {

		@Autowired
		private ISupplierRepository proveedorRepository;
	
		public List<Supplier> listSuppliers (){
		return proveedorRepository.findAll();
		}
	
		public boolean alReadyExist(Long id) {
		Optional<Supplier> supplierExist = proveedorRepository.findById(id);
		return supplierExist.isPresent();
		}

		public Supplier getById(Long id) {
	        return proveedorRepository.findById(id).orElse(null);
	    }

	    public Supplier save(Supplier supplier) {
	        return proveedorRepository.save(supplier);
	    }

	    public void delete(Long id) {
	    	proveedorRepository.deleteById(id);
	    }
}
