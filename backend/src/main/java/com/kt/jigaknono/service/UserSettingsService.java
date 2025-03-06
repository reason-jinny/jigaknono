package com.kt.jigaknono.service;

import com.kt.jigaknono.domain.UserSettings;
import com.kt.jigaknono.repository.UserSettingsRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    // 사용자 설정 저장
    public UserSettings saveUserSettings(UserSettings userSettings) {
        return userSettingsRepository.save(userSettings);
    }

    // 세션 ID로 설정 조회
    public Optional<UserSettings> getUserSettingsBySessionId(String sessionId) {
        return userSettingsRepository.findBySessionId(sessionId);
    }

    // 모든 사용자 설정 조회
    public List<UserSettings> getAllUserSettings() {
        return userSettingsRepository.findAll();
    }
}