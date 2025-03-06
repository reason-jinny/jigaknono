package com.kt.jigaknono.controller;

import com.kt.jigaknono.domain.UserSettings;
import com.kt.jigaknono.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-settings")
public class UserSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    // 사용자 설정 저장
    @PostMapping
    public ResponseEntity<UserSettings> saveUserSettings(@RequestBody UserSettings userSettings) {
        UserSettings savedSettings = userSettingsService.saveUserSettings(userSettings);
        return new ResponseEntity<>(savedSettings, HttpStatus.CREATED);
    }

    // 세션 ID로 설정 조회
    @GetMapping("/{sessionId}")
    public ResponseEntity<UserSettings> getUserSettingsBySessionId(@PathVariable String sessionId) {
        UserSettings userSettings = userSettingsService.getUserSettingsBySessionId(sessionId);
        return userSettings != null
                ? new ResponseEntity<>(userSettings, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}