package com.t1.tripfy.domain.dto;

import java.util.List;

import com.t1.tripfy.domain.dto.user.UserImgDTO;

import lombok.Data;

@Data
public class ReviewDTO {
	private long guidenum;
	private String userid;
	private String contents;
	private long packagenum;
	private String emSysname;
	
    private List<UserImgDTO> userImages;
    
    // getter와 setter 추가
    public List<UserImgDTO> getUserImages() {
        return userImages;
    }

    public void setUserImages(List<UserImgDTO> userImages) {
        this.userImages = userImages;
    }
}
