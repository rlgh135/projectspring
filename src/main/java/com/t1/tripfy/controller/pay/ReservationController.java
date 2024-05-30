package com.t1.tripfy.controller.pay;

import com.t1.tripfy.domain.dto.ReservationDTO;
import com.t1.tripfy.service.pack.PackageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class ReservationController {
    @Autowired
    private PackageService service;

    @PostMapping("/save_payment")
    public ResponseEntity<Map<String, Boolean>> savePayment(@RequestBody ReservationDTO reservationDTO) {
        try {
            if (reservationDTO.getUserid().equals("비회원")) {
                service.saveReservationForNonMember(reservationDTO);
            } else {
                service.saveReservationForMember(reservationDTO);
            }
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
