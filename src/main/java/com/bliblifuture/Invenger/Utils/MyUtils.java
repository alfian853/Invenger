package com.bliblifuture.Invenger.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MyUtils {

    private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(MyUtils.class);

    public static String getBcryptHash(String s){return bCryptPasswordEncoder.encode(s);}
    public static boolean matches(String plain, String encoded){
        return bCryptPasswordEncoder.matches(plain,encoded);
    }

    public static void log(String s){
        logger.info(s);
    }



}
