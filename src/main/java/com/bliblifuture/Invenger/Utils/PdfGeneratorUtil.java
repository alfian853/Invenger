package com.bliblifuture.Invenger.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Component
public class PdfGeneratorUtil {
    @Autowired
    private TemplateEngine templateEngine;
    public void createPdf(String templateName, Map map) throws Exception{
        Assert.notNull(templateName, "Template name can't be null");
        Context ctx = new Context();
        if(map != null){
            Iterator itMap = map.entrySet().iterator();
            while (itMap.hasNext()){
                Map.Entry pair = (Map.Entry) itMap.next();
                ctx.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }
        String processedHtml = templateEngine.process(templateName, ctx);
        FileOutputStream outputStream = null;
        String fileName = UUID.randomUUID().toString();
        try {
            final File outputFile = File.createTempFile(fileName, ".pdf");
            outputStream = new FileOutputStream(outputFile);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();
            System.out.println(outputFile);
            System.out.println("PDF created successfully");
        }
        finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                }catch (IOException e){}
            }
        }
    }
}