package com.example.springsecurityv1.repository;

import com.example.springsecurityv1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// CRUD
// query method
// @Repository annotation 없어도 IoC 된다! JpaRepository 를 상속받았기 때문
public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByUsername(String username);
}
