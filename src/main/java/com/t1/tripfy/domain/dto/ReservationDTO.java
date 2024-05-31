package com.t1.tripfy.domain.dto;

import lombok.Data;

@Data
public class ReservationDTO {
    private long reservationnum;
    private long packagenum;
    private int adultCnt;
    private int childCnt;
    private String userid;
    private String phone;
    private String email;
    private String keycode;
    private String price;
    private String payMethod;
    private int isDelete;
    private String name;
    
    private int currentCount;

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }
}
