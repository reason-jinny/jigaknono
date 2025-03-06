package com.kt.jigaknono.controller;

import com.kt.jigaknono.domain.UserSettings;
import com.kt.jigaknono.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {

    @Autowired
    private UserSettingsService userSettingsService;

    // 추천 경로 및 출발 시간 조회
    @GetMapping("/{sessionId}")
    public ResponseEntity<String> getRecommendation(@PathVariable String sessionId) {
        Optional<UserSettings> userSettingsOptional = userSettingsService.getUserSettingsBySessionId(sessionId);
        
        if (userSettingsOptional.isPresent()) {
            UserSettings userSettings = userSettingsOptional.get();
            String preference = userSettings.getPreference();
            String recommendation;

            // 편안함을 선택하면 셔틀을 타는 기준, 빠른 도착은 가장 빠른 루트
            if ("편안함".equals(preference)) {
                recommendation = "셔틀 기준으로 출발 시간을 계산합니다.";
            } else {
                recommendation = "가장 빠른 루트 기준으로 출발 시간을 계산합니다.";
            }

            return new ResponseEntity<>(recommendation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("사용자 설정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
}