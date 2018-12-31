package com.bliblifuture.invenger.Utils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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

    public String getRandomFileName(MultipartFile file){
        return UUID.randomUUID().toString().replace("-","")+
                "."+ FilenameUtils.getExtension(file.getOriginalFilename());
    }
    public String getRandomFileName(String extension){
        return UUID.randomUUID().toString().replace("-","")+
                "."+ extension;
    }



}
