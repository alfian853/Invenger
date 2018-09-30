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
import java.util.HashMap;

@Service
public class FileStorageService {
    private final String BASE_PATH = "src/main/resources";
    private final String USER_PROFILE_PATH = BASE_PATH+"/profile-pict/";
    public enum PathCategory{
        PROFILE_PICT,INVENTORY_PICT
    }
    private final HashMap<PathCategory,String> path = new HashMap<PathCategory,String>(){{
      put(PathCategory.PROFILE_PICT,BASE_PATH+"/profile-pict/");
      put(PathCategory.INVENTORY_PICT,BASE_PATH+"/inventory-pict/");
    }};

    public boolean storeFile(MultipartFile file, String fileName, PathCategory pathCategory){
        Path target = null;
        try{
            if(fileName.contains("..")) {
                return false;
            }
            target = Paths.get(this.path.get(pathCategory)+"/"+fileName).toAbsolutePath().normalize();
            Files.copy(file.getInputStream(),target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
