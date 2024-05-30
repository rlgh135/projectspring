package com.t1.tripfy.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //타임라인 파일관리 폴더 매핑
        registry.addResourceHandler("/tilelineThumnail/**")
                .addResourceLocations("file:///D:/spring_yes/tempFile/");
        //보드 파일관리 폴더 매핑
        registry.addResourceHandler("/BoardThumnail/**")
//        		.addResourceLocations("file:///D:/spring_yes/boardfile/");
//        		.addResourceLocations("file:///C:/Users/USER/Desktop/spring/file/");
        
        		.addResourceLocations("file:///D:/spring_yes/boardfile/");
        //보드 썸머노트 파일관리 폴더 매핑
        registry.addResourceHandler("/BoardSummerNoteThumnail/**")
				.addResourceLocations("file:///D:/spring_yes/boardSummerNotefile/");
    }
}
