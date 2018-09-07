package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
}
