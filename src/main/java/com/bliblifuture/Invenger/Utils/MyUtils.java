package com.bliblifuture.Invenger.Utils;

import com.bliblifuture.Invenger.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class MyUtils {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final Logger logger = LoggerFactory.getLogger(MyUtils.class);

    public String getBcryptHash(String s){return bCryptPasswordEncoder.encode(s);}

    public boolean matches(String plain, String encoded){
        return bCryptPasswordEncoder.matches(plain,encoded);
    }

    public void log(String s){
        logger.info(s);
    }

}
