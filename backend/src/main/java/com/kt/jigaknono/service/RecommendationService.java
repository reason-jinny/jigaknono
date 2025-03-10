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

    // 🔄 도착 시간 계산
    public LocalTime calculateArrivalTime(TransportSchedule schedule) {
        return schedule.getDepartureTime()
                .plusMinutes(Optional.ofNullable(schedule.getDuration()).orElse(0))
                .plusMinutes(Optional.ofNullable(schedule.getTrafficDelay()).orElse(0))
                .plusMinutes(Optional.ofNullable(schedule.getWalkDuration()).orElse(0));
    }

    // 🔄 경로 추천 및 출발 시간 계산
    public Map<String, Object> recommendRoute(String currentLocation, String targetArriavalTimeStr) {

        // 🔄 목표 도착 시간 파라미터를 LocalTime으로 변환
        LocalTime targetArrivalTime = LocalTime.parse(targetArriavalTimeStr);

        // 출발지 이름 매핑
        String mappedLocation = mapLocation(currentLocation);

        // 가능한 모든 도착지에 대해 스케줄 검색
        // 스케줄 검색 시 endLocation 파라미터 제거
        Optional<TransportSchedule> optionalSchedule = transportScheduleRepository
            .findTopByStartLocationContainingAndCalculatedArrivalTimeBeforeOrderByDepartureTimeDesc(
                mappedLocation, targetArrivalTime);

        return optionalSchedule.map(schedule -> {
            // 데이터가 있을 때: schedule을 받아서 Map<String, Object> 형태로 가공
            Map<String, Object> result = new HashMap<>();
            result.put("departureTime", schedule.getDepartureTime());
            result.put("arrivalTime", calculateArrivalTime(schedule)); // 🔄 Service에서 계산한 도착 시간
            result.put("startLocation", schedule.getStartLocation());
            result.put("routeNumber", schedule.getRouteNumber());
            result.put("recommendedRoute", schedule.getRouteType() + " " + schedule.getRouteNumber());
            result.put("status", "success");
            return result;
        // 데이터가 없을 때: map이 호출되지 않고 orElseGet이 실행 - 에러 메시지 반환
        // orElseGet: 데이터가 없을 때 실행되는 람다식 - Optional이 비어있을 때 실행될 코드를 작성
        }).orElseGet(() -> createErrorResult("적절한 경로를 찾을 수 없습니다."));                
    }

    private String mapLocation(String location) {
        switch (location) {
            case "판교역":
                return "판교역";  // 이제 "Contains" 검색을 하므로 정확한 매칭이 필요없음
            case "청계산입구역":
                return "청계산입구역";
            default:
                return location;
        }
    }

    // 🔄 에러 메시지 생성
    private Map<String, Object> createErrorResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("message", message);
        return result;
    }
}