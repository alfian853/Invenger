package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.model.Role;
import com.bliblifuture.Invenger.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public List<Role> getAllRole(){
        return roleRepository.findAll();
    }
}
