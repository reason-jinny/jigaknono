// package com.kt.jigaknono.domain;

// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;

// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.util.List;

// @Entity
// @Getter
// @Setter
// public class UserSettings {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "session_id", unique = true) // 고유 세션 ID, Unique 설정
//     private String sessionId;

//     // private String currentLocation; 
//     @Enumerated(EnumType.STRING) // 출발 지역을 "청계산입구역"과 "판교역" 두 가지로 한정함
//     private Station currentLocation;

//     private String destination = "KT판교빌딩"; // 기본값 설정

//     private LocalTime targetArrivalTime; // 목표 출근 시간 
//     // TransportSchedule의 'arrivalTime'과 헷갈릴 것 같아서 변수명 수정

//     private LocalDateTime createdAt = LocalDateTime.now();
    
//     private LocalDateTime updatedAt = LocalDateTime.now();

//     @OneToMany(mappedBy = "userSettings", cascade = CascadeType.ALL)
//     private List<AdditionalSettings> additionalSettings;
// }