package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.UserRole;
import com.example.demo.responsitory.UserResponsitory;
import com.example.demo.responsitory.UserRoleResponsitory;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserResponsitory userRepo;

	@Autowired
	private UserRoleResponsitory repo;

	public List<AppUser> listAll() {
		return userRepo.findAll();
	}

	public void save(AppUser user) {
		userRepo.save(user);
	}

	public AppUser get(long id) {
		return userRepo.findById(id).get();
	}

	public void delete(long id) {
		userRepo.deleteById(id);
	}

	public UserRole findByUserId(long id) {
		UserRole userRole = repo.getOne(id);
		return userRole;

	}
	public AppUser findByUserName(String username) {
		return userRepo.findByUserName(username);

	}
	public AppUser findByEmail(String email) {
		return userRepo.findByEmail(email);

	}
	
	 public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException {
	        AppUser customer = userRepo.findByEmail(email);
	        if (customer != null) {
	            customer.setResetPasswordToken(token);
	            userRepo.save(customer);
	        } else {
	            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
	        }
	    }
	     
	    public AppUser getByResetPasswordToken(String token) {
	        return userRepo.findByResetPasswordToken(token);
	    }
	     
	    public void updatePassword(AppUser customer, String newPassword) {
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	        String encodedPassword = passwordEncoder.encode(newPassword);
	        customer.setEncrytedPassword(encodedPassword);
	         
	        customer.setResetPasswordToken(null);
	        userRepo.save(customer);
	    }
}
