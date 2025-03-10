package com.kt.jigaknono.repository;

import com.kt.jigaknono.domain.TransportSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface TransportScheduleRepository extends JpaRepository<TransportSchedule, Long> {

    // 📌 출발 위치와 도착 위치 + 목표 도착 시간 이전의 스케줄 중 가장 늦은 출발 시간 조회
    Optional<TransportSchedule> findTopByStartLocationContainingAndEndLocationAndArrivalTimeBeforeOrderByDepartureTimeDesc(
            String startLocation, String endLocation, LocalTime targetArrivalTime);
}

// 쿼리 메서드: JPA Repository를 사용할 때 우리가 만드는 메서드
// 장점: 함수 이름만으로 SQL을 자동으로 생성해주는 편리함