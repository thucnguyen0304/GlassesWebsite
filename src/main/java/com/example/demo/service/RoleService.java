package com.example.demo.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AppRole;
import com.example.demo.responsitory.RoleResponsitory;

@Service
@Transactional
public class RoleService {
 
    @Autowired
    private RoleResponsitory repo;
     
    public List<AppRole> listAll() {
        return repo.findAll();
    }
     
    public void save(AppRole role) {
        repo.save(role);
    }
     
    public AppRole get(long id) {
        return repo.findById(id).get();
    }
     
    public void delete(long id) {
        repo.deleteById(id);
    }

}
