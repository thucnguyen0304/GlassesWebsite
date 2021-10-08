package com.example.demo.responsitory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AppRole;

public interface RoleResponsitory extends JpaRepository<AppRole, Long> {

}
