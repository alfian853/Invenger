package com.bliblifuture.invenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
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

        registry.addResourceHandler("/file/*")
                .addResourceLocations(BASE_PATH+"/static/file/");

        registry.addResourceHandler("/inventory/pict/*")
                .addResourceLocations(BASE_PATH+ "/inventory/pict/");

        registry.addResourceHandler("/inventory/document/*")
                .addResourceLocations(BASE_PATH+ "/inventory/document/");

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }
}
