package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer> {
    Admin findByName(String name);
}
