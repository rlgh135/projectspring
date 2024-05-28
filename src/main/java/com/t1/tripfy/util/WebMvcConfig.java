package com.t1.tripfy.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /tilelineThumnail/** 경로로 들어오는 요청을 file:///D:/spring_yes/tempFile/ 경로의 리소스와 매핑
        registry.addResourceHandler("/tilelineThumnail/**")
                .addResourceLocations("file:///D:/spring_yes/tempFile/");
    }
}
