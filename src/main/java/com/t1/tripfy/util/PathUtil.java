package com.t1.tripfy.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class PathUtil {
	private static String getUserStaticFolder() {
		return System.getProperty("user.dir")+"\\src\\main\\resources\\static\\";
	}
	
	public static String writeImageFile(MultipartFile thumbnail){
        UUID uuid = UUID.randomUUID();
        String sysname = uuid+"_"+thumbnail.getOriginalFilename(); //DB에 저장될 이름
        String uuidImageRealName = "\\images\\userimg\\userthumbnail\\"+uuid+"_"+thumbnail.getOriginalFilename(); //저장소에 저장될 이름
        String staticFolder = getUserStaticFolder();
        Path imageFilePath = Paths.get(staticFolder+"\\"+uuidImageRealName);
        try {
            Files.write(imageFilePath, thumbnail.getBytes()); // 내부적으로 비동기. .. 스레드가 있음
        }catch (Exception e){
        	System.out.println("사진을 웹서버에 저장하지 못했습니다");
        }
        return sysname;
    }
}
