package com.example.demo.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.UserRole;
import com.example.demo.responsitory.UserRoleResponsitory;


@Service
@Transactional
public class UserRoleService {

	@Autowired
	private UserRoleResponsitory repo;

	public List<UserRole> listAll() {
		return repo.findAll();
	}

	public void save(UserRole userRole) {
		repo.save(userRole);
	}

	public UserRole get(long id) {
		return repo.findById(id).get();
	}

	public void delete(long id) {
		repo.deleteById(id);
	}
	
	public UserRole findByUserId(AppUser user) {
		return repo.findByAppUser(user);
	}
}
