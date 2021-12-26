package com.example.springsecurityv1.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Data
public class User {

    @Id  // pk  (javax.persistence.Id)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    /* ROLE
    * 1. ROLE_USER
    * 2. ROLE_MANAGER
    * 3. ROLE_ADMIN
    * */
    private String role;

    @CreationTimestamp
    private Timestamp createDate;
}
