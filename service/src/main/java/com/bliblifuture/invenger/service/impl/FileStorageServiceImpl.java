package com.bliblifuture.invenger.service.impl;

import com.bliblifuture.invenger.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    ITemplateEngine templateEngine;

    public boolean storeFile(MultipartFile file, String fileName, PathCategory pathCategory){
        Path target;
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

    public String createPdfFromTemplate(String fileName, String templateName, Map<String,String> data, PathCategory pathCategory){

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

        OutputStream outputStream = null;

        try {
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
