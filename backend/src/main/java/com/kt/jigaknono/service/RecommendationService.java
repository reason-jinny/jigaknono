package com.kt.jigaknono.service;

import com.kt.jigaknono.domain.TransportSchedule;
// import com.kt.jigaknono.domain.UserSettings;
import com.kt.jigaknono.repository.TransportScheduleRepository;
// import com.kt.jigaknono.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RecommendationService {

    @Autowired 
    private TransportScheduleRepository transportScheduleRepository;

    // 🔄 경로 추천 및 출발 시간 계산
    public Map<String, Object> recommendRoute(String currentLocation, String targetArriavalTimeStr) {

        // 🔄 목표 도착 시간 파라미터를 LocalTime으로 변환
        LocalTime targetArrivalTime = LocalTime.parse(targetArriavalTimeStr);

        // 📌 가장 늦은 출발 시간 하나를 가져옴
        Optional<TransportSchedule> optionalSchedule = transportScheduleRepository
                .findTopByStartLocationAndEndLocationAndArrivalTimeBeforeOrderByDepartureTimeDesc(
                        currentLocation, "KT판교빌딩", targetArrivalTime);
        
        // 🔄 데이터를 가공해 `Map`으로 반환
        return optionalSchedule.map(schedule -> {
            Map<String, Object> result = new HashMap<>();
            result.put("departureTime", schedule.getDepartureTime());
            result.put("arrivalTime", schedule.getArrivalTime());
            result.put("recommendedRoute", schedule.getRouteType() + " " + schedule.getRouteNumber());
            result.put("status", "success");
            return result;
        }).orElseGet(() -> createErrorResult("적절한 경로를 찾을 수 없습니다."));                
    }

    // 🔄 에러 메시지 생성
    private Map<String, Object> createErrorResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("message", message);
        return result;
    }
}