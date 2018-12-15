package com.bliblifuture.invenger.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableJpaAuditing
public class MvcConfig implements WebMvcConfigurer {

    final String BASE_PATH = "file:web/src/main/resources";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile/pict/*")
        .addResourceLocations(BASE_PATH+"/profile-pict/");

        registry.addResourceHandler("/css/*")
                .addResourceLocations(BASE_PATH+"/static/css/");

        registry.addResourceHandler("/js/*")
                .addResourceLocations(BASE_PATH+"/static/js/");

        registry.addResourceHandler("/inventory/pict/*")
                .addResourceLocations(BASE_PATH+ "/inventory/pict/");

        registry.addResourceHandler("/inventory/document/*")
                .addResourceLocations(BASE_PATH+ "/inventory/document/");
    }
}
