package com.kt.jigaknono.service;

import com.kt.jigaknono.domain.UserSettings;
import com.kt.jigaknono.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class RouteRecommendationService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    // 경로 추천 및 출발 시간 계산
    public Map<String, Object> recommendRoute(String sessionId) {
        UserSettings userSettings = userSettingsRepository.findBySessionId(sessionId).orElse(null);
        if (userSettings == null) {
            return null;
        }

        // 사용자 설정에 따라 출발 시간 계산 (임시 로직)
        LocalTime arrivalTime = userSettings.getArrivalTime();
        String preference = userSettings.getPreference();

        LocalTime departureTime;
        String recommendedRoute;

        if ("편안함".equals(preference)) {
            // 편안함: 무조건 셔틀 이용
            recommendedRoute = "KT 셔틀 + 대중교통";
            departureTime = calculateShuttleDepartureTime(arrivalTime); // 셔틀 출발 시간 계산
        } else {
            // 빠른 도착: 가장 빠른 루트 탐색
            recommendedRoute = findFastestRoute(); // 가장 빠른 경로 탐색 (임시)
            departureTime = calculateFastestDepartureTime(arrivalTime); // 빠른 도착 출발 시간 계산 (임시)
        }

        // 결과 맵 구성
        Map<String, Object> result = new HashMap<>();
        result.put("departureTime", departureTime);
        result.put("arrivalTime", arrivalTime);
        result.put("recommendedRoute", recommendedRoute);

        return result;
    }

    // 🟢 셔틀 출발 시간 계산 (임시 로직)
    private LocalTime calculateShuttleDepartureTime(LocalTime arrivalTime) {
        return arrivalTime.minusMinutes(15); // 셔틀은 15분 일찍 출발한다고 가정
    }

    // 🟢 가장 빠른 출발 시간 계산 (임시 로직)
    private LocalTime calculateFastestDepartureTime(LocalTime arrivalTime) {
        return arrivalTime.minusMinutes(10); // 빠른 도착은 10분 일찍 출발한다고 가정
    }

    // 🟢 가장 빠른 경로 탐색 (임시 로직)
    private String findFastestRoute() {
        return "대중교통 + 도보"; // 임시로 가장 빠른 경로를 대중교통 + 도보로 설정
    }
}