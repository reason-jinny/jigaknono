package com.kt.jigaknono.service;

import com.kt.jigaknono.domain.TransportSchedule;
// import com.kt.jigaknono.domain.UserSettings;
import com.kt.jigaknono.repository.TransportScheduleRepository;
// import com.kt.jigaknono.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

        // 출발지 이름 매핑
        String mappedLocation = mapLocation(currentLocation);

        // 도착지 목록 - 여러 종류의 도착지를 모두 포함
        List<String> possibleEndLocations = Arrays.asList(
            "KT판교빌딩", 
            "스마트모빌리티실증허브",
            "벤처타운(남문)"
        );

        // 가능한 모든 도착지에 대해 스케줄 검색
        Optional<TransportSchedule> optionalSchedule = possibleEndLocations.stream()
            .map(endLocation -> transportScheduleRepository
                .findTopByStartLocationContainingAndEndLocationAndArrivalTimeBeforeOrderByDepartureTimeDesc(
                    mappedLocation, endLocation, targetArrivalTime))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

        return optionalSchedule.map(schedule -> {
            // 데이터가 있을 때: schedule을 받아서 Map<String, Object> 형태로 가공
            Map<String, Object> result = new HashMap<>();
            result.put("departureTime", schedule.getDepartureTime());
            result.put("arrivalTime", schedule.getArrivalTime());
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