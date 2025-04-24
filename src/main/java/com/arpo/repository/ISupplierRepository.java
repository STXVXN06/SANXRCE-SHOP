package com.arpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arpo.models.Supplier;

public interface ISupplierRepository extends JpaRepository<Supplier, Long>{

}
