package com.kt.jigaknono.repository;

import com.kt.jigaknono.domain.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    // 세션 ID로 사용자 설정 조회
    Optional<UserSettings> findBySessionId(String sessionId);
}