// package com.kt.jigaknono.domain;

// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;

// @Entity
// @Getter
// @Setter
// public class AdditionalSettings {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private int entranceTime; // 집에서 1층까지 걸리는 시간 (분 단위)

//     private int officeTime; // KT 빌딩 내 근무지까지 걸리는 시간 (분 단위)

//     @ManyToOne
//     @JoinColumn(name = "user_settings_id")
//     private UserSettings userSettings; // UserSettings와의 N:1 관계
// }