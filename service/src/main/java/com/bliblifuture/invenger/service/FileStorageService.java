package com.bliblifuture.invenger.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

public interface FileStorageService {

    enum PathCategory{
        PROFILE_PICT, INVENTORY_PICT, INVENTORY_PDF
    }

    String BASE_PATH = "web/src/main/resources";

    HashMap<PathCategory,String> path = new HashMap<PathCategory,String>(){{
        put(PathCategory.PROFILE_PICT,BASE_PATH+"/profile-pict/");
        put(PathCategory.INVENTORY_PICT,BASE_PATH+ "/inventory/pict/");
        put(PathCategory.INVENTORY_PDF,BASE_PATH+"/inventory/document/");
    }};


    boolean storeFile(MultipartFile file, String fileName, PathCategory pathCategory);

    boolean deleteFile(String fileName,PathCategory pathCategory);

    String createPdfFromTemplate(String fileName, String templateName, Map<String,String> data, PathCategory pathCategory);

    }
