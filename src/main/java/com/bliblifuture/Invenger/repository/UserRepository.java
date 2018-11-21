package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User> {
    User findUserById(Integer id);
    User findByUsername(String username);
    User findByEmail(String email);

    @Query("select u from User u LEFT JOIN FETCH u.position LEFT JOIN FETCH u.superior")
    List<User> findAllFetched();

}

