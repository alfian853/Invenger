package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.InvengerApplication;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Service
public class UserServiceImp implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        InvengerApplication.log.info("masuk sayang");
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = findByUsername(s);
        InvengerApplication.log.info("masuk sayang2 "+ Boolean.toString(user == null));
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
