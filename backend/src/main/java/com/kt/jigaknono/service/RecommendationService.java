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

    // // 🔄 경로 추천 및 출발 시간 계산
    // public Map<String, Object> recommendRoute(String currentLocation, String targetArriavalTimeStr, int weatherDelay) {
    //     LocalTime targetArrivalTime = LocalTime.parse(targetArriavalTimeStr);
    //     String mappedLocation = mapLocation(currentLocation);

    //     Optional<TransportSchedule> optionalSchedule = transportScheduleRepository
    //         .findTopByStartLocationContainingAndCalculatedArrivalTimeBeforeOrderByDepartureTimeDesc(
    //             mappedLocation, targetArrivalTime);

    //     return optionalSchedule.map(schedule -> {
    //         Map<String, Object> result = new HashMap<>();
    //         result.put("departureTime", schedule.getDepartureTime());
    //         result.put("arrivalTime", calculateArrivalTime(schedule).plusMinutes(weatherDelay));
    //         result.put("startLocation", schedule.getStartLocation());
    //         result.put("routeNumber", schedule.getRouteNumber());
    //         result.put("recommendedRoute", schedule.getRouteType() + " " + schedule.getRouteNumber());
    //         result.put("status", "success");
    //         return result;
    //     }).orElseGet(() -> createErrorResult("적절한 경로를 찾을 수 없습니다."));
    // }
    // 🔄 경로 추천 및 출발 시간 계산
    public Map<String, Object> recommendRoute(String currentLocation, String targetArriavalTimeStr, int weatherDelay) {
        LocalTime targetArrivalTime = LocalTime.parse(targetArriavalTimeStr);

        // 날씨 지연을 고려하여 실제 목표 도착 시간을 더 일찍으로 조정
        LocalTime adjustedTargetTime = targetArrivalTime.minusMinutes(weatherDelay);

        String mappedLocation = mapLocation(currentLocation);

        Optional<TransportSchedule> optionalSchedule = transportScheduleRepository
                .findTopByStartLocationContainingAndCalculatedArrivalTimeBeforeOrderByDepartureTimeDesc(
                        mappedLocation, adjustedTargetTime);

        return optionalSchedule.map(schedule -> {
            Map<String, Object> result = new HashMap<>();
            result.put("departureTime", schedule.getDepartureTime());
            
            // 버스 하차 시간 계산 (도보 시간 제외)
            LocalTime busArrivalTime = schedule.getDepartureTime()
                .plusMinutes(Optional.ofNullable(schedule.getDuration()).orElse(0))
                .plusMinutes(Optional.ofNullable(schedule.getTrafficDelay()).orElse(0));
            
            result.put("busArrivalTime", busArrivalTime);
            result.put("arrivalTime", calculateArrivalTime(schedule));
            result.put("startLocation", schedule.getStartLocation());
            result.put("endLocation", schedule.getEndLocation());
            result.put("routeNumber", schedule.getRouteNumber());
            result.put("walkDuration", schedule.getWalkDuration());
            result.put("recommendedRoute", schedule.getRouteType() + " " + schedule.getRouteNumber());
            result.put("status", "success");
            return result;
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