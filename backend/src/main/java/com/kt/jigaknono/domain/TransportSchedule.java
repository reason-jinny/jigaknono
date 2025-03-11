package com.kt.jigaknono.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "transport_schedule") // 테이블명과 매핑
public class TransportSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_type")
    private String routeType; // 셔틀 또는 버스 - 예: 셔틀, 버스

    @Column(name = "route_number")
    private String routeNumber; // 노선 번호 또는 셔틀 번호 - 예: 서울07출근, 55, 310

    @Column(name = "start_location")
    private String startLocation; // 출발지 - 예: 청계산입구역, 판교역

    @Column(name = "end_location")
    private String endLocation;

    @Column(name = "departure_time")
    private LocalTime departureTime; // 출발 시간

    @Column(name = "duration")
    private Integer duration;  // 기본 소요 시간 (분)

    @Column(name = "traffic_delay")
    private Integer trafficDelay;  // 교통 지연 시간 (분)

    @Column(name = "walk_duration")
    private Integer walkDuration;  // 도보 소요 시간 (분)

    @Column(name = "walk_distance")
    private Integer walkDistance = 0; // 기본값 설정

    // 🔄 도착 시간 계산 메서드 제거 (Service로 이동)
}