package com.civifix.civifix_backend.repository;

import com.civifix.civifix_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { //save() findall() /findbyid() count() deleteUserbyId()
    boolean existsByEmail(String email);  //srping boot read name and create SQL query itself
    Optional<User> findByEmail(String email);
}
