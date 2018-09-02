package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String role_name);
}
