package com.example.demo.responsitory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AppUser;

public interface UserResponsitory extends JpaRepository<AppUser, Long> {

	AppUser findByUserName(String username);

	AppUser findByEmail(String email);

	AppUser findByResetPasswordToken(String token);

}
