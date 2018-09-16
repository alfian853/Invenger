package com.bliblifuture.Invenger.service;


import com.bliblifuture.Invenger.InvengerApplication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final static String BASE_PATH = "src/main/resources";
    private final static String USER_PROFILE_PATH = BASE_PATH+"/profile-pict/";

    public static enum PathCategory{
        PROFILE_PICT
    }

    public boolean storeFile(MultipartFile file, String fileName, PathCategory path){
        Path target = null;
        try{
            if(fileName.contains("..")) {
                return false;
            }
            target = Paths.get(USER_PROFILE_PATH+"/"+fileName).toAbsolutePath().normalize();
            Files.copy(file.getInputStream(),target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}
