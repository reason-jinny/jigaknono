package com.kt.jigaknono.repository;

import com.kt.jigaknono.domain.TransportSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface TransportScheduleRepository extends JpaRepository<TransportSchedule, Long> {

    // 📌 출발 위치 조건 + 목표 도착 시간 이전(같을 수도 있음)의 스케줄 중 가장 늦은 출발 시간 조회
    @Query(value = "SELECT * FROM transport_schedule ts WHERE ts.start_location ILIKE '%' || :startLocation || '%' " +
            "AND ts.departure_time + (ts.duration + COALESCE(ts.traffic_delay, 0) + COALESCE(ts.walk_duration, 0)) * INTERVAL '1 minute' <= :arrivalTime "
            +
            "ORDER BY ts.departure_time DESC LIMIT 1", nativeQuery = true)
    Optional<TransportSchedule> findTopByStartLocationContainingAndCalculatedArrivalTimeBeforeOrderByDepartureTimeDesc(
            @Param("startLocation") String startLocation,
            @Param("arrivalTime") LocalTime arrivalTime);
}

// 쿼리 메서드: JPA Repository를 사용할 때 우리가 만드는 메서드
// 장점: 함수 이름만으로 SQL을 자동으로 생성해주는 편리함