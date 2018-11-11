package com.bliblifuture.Invenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    @Autowired
    InventoryService inventoryService;

    @Autowired
    TemplateEngine templateEngine;

    private final String BASE_PATH = "src/main/resources";

    public enum PathCategory{
        PROFILE_PICT,INVENTORY_PICT, INVENTORY_PDF
    }

    private final HashMap<PathCategory,String> path = new HashMap<PathCategory,String>(){{
      put(PathCategory.PROFILE_PICT,BASE_PATH+"/profile-pict/");
      put(PathCategory.INVENTORY_PICT,BASE_PATH+ "/inventory/");
      put(PathCategory.INVENTORY_PDF,BASE_PATH+"/inventory/document/");
    }};

    public String getFilePath(PathCategory pathCategory){
        return path.get(pathCategory);
    }

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

    public boolean deleteFile(String fileName,PathCategory pathCategory){
        try{
            Path target = Paths.get(this.path.get(pathCategory)+"/"+fileName).toAbsolutePath();
            Files.delete(target);
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public String createPdfFromTemplate(String templateName, Map<String,String> data, PathCategory pathCategory){

        if(templateName == null){
            return null;
        }

        Context ctx = new Context();
        if(data != null){
            Iterator itMap = data.entrySet().iterator();
            while (itMap.hasNext()){
                Map.Entry pair = (Map.Entry) itMap.next();
                ctx.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }

        String processedHtml = templateEngine.process(templateName, ctx);

        FileOutputStream outputStream = null;

        String fileName = UUID.randomUUID().toString() + ".pdf";

        try {
//            final File outputFile = File.createTempFile(fileName, ".pdf",new File(path.get(pathCategory)));
            final File outputFile = new File(path.get(pathCategory)+fileName);
            outputStream = new FileOutputStream(outputFile);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();


            System.out.println(outputFile);
            System.out.println("INVENTORY_PDF created successfully");

            return fileName;
        }
        catch(Exception e){
            e.printStackTrace();
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }

    }

}
