package com.bliblifuture.Invenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    final String BASE_PATH = "file:src/main/resources";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile/pict/*")
        .addResourceLocations(BASE_PATH+"/profile-pict/");

        registry.addResourceHandler("/css/*")
                .addResourceLocations(BASE_PATH+"/static/css/");

        registry.addResourceHandler("/js/*")
                .addResourceLocations(BASE_PATH+"/static/js/");

        registry.addResourceHandler("/inventory/pict/*")
                .addResourceLocations(BASE_PATH+"/inventory-pict/");
    }
}
