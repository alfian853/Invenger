package com.bliblifuture.Invenger.service.Imp;

import com.bliblifuture.Invenger.InvengerApplication;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.RoleRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    /*
    * load by username or email for login and auto login purpose
    * */
    @Override
    public User loadUserByUsername(String s) throws UsernameNotFoundException {
        InvengerApplication.log.info("iam called "+s);
        User user =userRepository.findByUsername(s);
        if(user == null){
            return userRepository.findByEmail(s);
        }
        return user;
    }



}
