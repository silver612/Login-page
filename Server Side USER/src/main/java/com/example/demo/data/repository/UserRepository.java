package com.example.demo.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.data.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

    @Query(value = "SELECT * FROM user WHERE username = ?1 LIMIT 1", nativeQuery = true)
    public Optional<User> findByName(String username);

}
