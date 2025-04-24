package com.arpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arpo.models.Bill;


public interface IBillRepository extends JpaRepository<Bill, Long>{

}
