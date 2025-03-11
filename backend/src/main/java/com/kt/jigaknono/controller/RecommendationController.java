package com.kt.jigaknono.controller;

import com.kt.jigaknono.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" })

public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    // 추천 경로 및 출발 시간 조회
    @GetMapping("/api/recommendation")
    public ResponseEntity<Map<String, Object>> getRecommendation(
            @RequestParam String currentLocation,
            @RequestParam String targetArrivalTimeStr,
            @RequestParam(required = false, defaultValue = "0") int weatherDelay) {

        // 🔄 추천 경로 및 시간 계산
        Map<String, Object> result = recommendationService.recommendRoute(
            currentLocation, 
            targetArrivalTimeStr,
            weatherDelay
        );

        // 🔄 에러 처리 및 응답
        if ("error".equals(result.get("status"))) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); // 에러 시 404 반환
        } else {
            return new ResponseEntity<>(result, HttpStatus.OK); // 정상 시 200 반환
        }
    }
}