package com.kt.jigaknono.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", unique = true) // 고유 세션 ID, Unique 설정
    private String sessionId;

    private String currentLocation;

    private String destination = "KT판교빌딩"; // 기본값 설정

    private LocalTime arrivalTime;

    private String preference; // 편안함 or 빠른 도착

    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "userSettings", cascade = CascadeType.ALL)
    private List<AdditionalSettings> additionalSettings;
}