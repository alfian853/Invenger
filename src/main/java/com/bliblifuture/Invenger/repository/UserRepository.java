package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.Position;
import com.bliblifuture.Invenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findUserById(Integer id);
    User findByUsername(String username);
    User findByEmail(String email);
}

