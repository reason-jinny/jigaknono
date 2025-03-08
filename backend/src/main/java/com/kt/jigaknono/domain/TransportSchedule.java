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
    private String endLocation = "KT판교빌딩"; // 도착지 - 기본값으로 KT판교빌딩 설정

    @Column(name = "departure_time")
    private LocalTime departureTime; // 출발 시간

    @Column(name = "arrival_time")
    private LocalTime arrivalTime; // 도착 시간
    // 도착 시간 = 출발 시간 + 기본 소요 시간 + 교통 지연 시간 + 도보 소요 시간
    // 인데 우선 그냥 하나의 데이터로 넣어서 만든 후 세분화 예정

    // @Column(name = "base_duration")
    // private Integer baseDuration; // 기본 소요 시간 (분)

    // @Column(name = "traffic_delay")
    // private int trafficDelay = 0; // 교통 지연 시간 (기본값 0)

    // @Column(name = "walk_duration")
    // private int walkDuration = 0; // 도보 소요 시간 (기본값 0)
}