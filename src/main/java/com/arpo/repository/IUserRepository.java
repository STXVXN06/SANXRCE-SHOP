package com.arpo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arpo.models.User;



public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
