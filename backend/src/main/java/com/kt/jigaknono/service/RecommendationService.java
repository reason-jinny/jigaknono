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
        // Repository에서 Optional<TransportSchedule> 형태로 데이터 조회
        // Optional: 데이터가 있을 수도 있고 없을 수도 있음 (null 체크를 쉽게 하기 위해 사용)
        Optional<TransportSchedule> optionalSchedule = transportScheduleRepository
                .findTopByStartLocationAndEndLocationAndArrivalTimeBeforeOrderByDepartureTimeDesc(
                        currentLocation, "KT판교빌딩", targetArrivalTime);
        
        // 🔄 데이터를 가공해 `Map`으로 반환 (에러 처리 및 반환 부분)
        // Map<String, Object> 형태로 반환
        // Map: key-value 형태(JSON 형태)로 데이터를 저장하는 자료구조
        return optionalSchedule.map(schedule -> {
            // 데이터가 있을 때: schedule을 받아서 Map<String, Object> 형태로 가공
            Map<String, Object> result = new HashMap<>();
            result.put("departureTime", schedule.getDepartureTime());
            result.put("arrivalTime", schedule.getArrivalTime());
            result.put("recommendedRoute", schedule.getRouteType() + " " + schedule.getRouteNumber());
            result.put("status", "success");
            return result;
        // 데이터가 없을 때: map이 호출되지 않고 orElseGet이 실행 - 에러 메시지 반환
        // orElseGet: 데이터가 없을 때 실행되는 람다식 - Optional이 비어있을 때 실행될 코드를 작성
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