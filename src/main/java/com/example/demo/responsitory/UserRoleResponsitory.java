package com.example.demo.responsitory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.UserRole;


public interface UserRoleResponsitory extends JpaRepository<UserRole, Long> {
	UserRole findByAppUser(AppUser user);
}
