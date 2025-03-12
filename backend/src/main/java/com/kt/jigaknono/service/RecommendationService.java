package com.kt.jigaknono.service;

import com.kt.jigaknono.domain.TransportSchedule;
// import com.kt.jigaknono.domain.UserSettings;
import com.kt.jigaknono.repository.TransportScheduleRepository;
// import com.kt.jigaknono.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Comparator;
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
    public Map<String, Object> recommendRoute(String currentLocation, String targetArriavalTimeStr, int weatherDelay, boolean preferShuttle) {
        LocalTime targetArrivalTime = LocalTime.parse(targetArriavalTimeStr);
        LocalTime adjustedTargetTime = targetArrivalTime.minusMinutes(weatherDelay);
        String mappedLocation = mapLocation(currentLocation);

        Optional<TransportSchedule> optionalSchedule = transportScheduleRepository
                .findTopByStartLocationContainingAndCalculatedArrivalTimeBeforeOrderByDepartureTimeDesc(
                        mappedLocation, adjustedTargetTime);

        return optionalSchedule.map(schedule -> {
            // 셔틀 선호 옵션이 켜져있고, 현재 스케줄이 셔틀이 아닌 경우
            if (preferShuttle && !isShuttle(schedule.getRouteNumber())) {
                // 다른 셔틀 스케줄을 찾아보기
                Optional<TransportSchedule> shuttleSchedule = transportScheduleRepository.findAll().stream()
                        .filter(s -> s.getStartLocation().contains(mappedLocation))
                        .filter(s -> isShuttle(s.getRouteNumber()))
                        .filter(s -> {
                            LocalTime arrival = calculateArrivalTime(s);
                            return !arrival.isAfter(adjustedTargetTime);
                        })
                        .max(Comparator.comparing(TransportSchedule::getDepartureTime));

                if (shuttleSchedule.isPresent()) {
                    schedule = shuttleSchedule.get();
                } else {
                    return createErrorResult("해당 시간에 이용 가능한 셔틀이 없습니다.\n일반 버스도 확인해보시겠어요? 🚌");
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("departureTime", schedule.getDepartureTime());
            
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
        }).orElseGet(() -> createErrorResult("너무 이른 시간에는 교통편이 없어요😭"));
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

    private boolean isShuttle(String routeNumber) {
        return routeNumber.toLowerCase().contains("kt") || 
               routeNumber.toLowerCase().contains("셔틀");
    }

    // 🔄 에러 메시지 생성
    private Map<String, Object> createErrorResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("message", message);
        return result;
    }
}