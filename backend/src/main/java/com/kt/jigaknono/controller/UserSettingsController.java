// package com.kt.jigaknono.controller;

// import com.kt.jigaknono.domain.UserSettings;
// import com.kt.jigaknono.service.UserSettingsService;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/user-settings")
// public class UserSettingsController {

//     @Autowired
//     private UserSettingsService userSettingsService;

//     // 사용자 설정 저장
//     @PostMapping
//     public ResponseEntity<UserSettings> saveUserSettings(@RequestBody UserSettings userSettings) {
//         UserSettings savedSettings = userSettingsService.saveUserSettings(userSettings);
//         return new ResponseEntity<>(savedSettings, HttpStatus.CREATED);
//     }

//     // 모든 사용자 설정 조회
//     @GetMapping
//     public ResponseEntity<List<UserSettings>> getAllUserSettings() {
//         List<UserSettings> userSettingsList = userSettingsService.getAllUserSettings();
//         return new ResponseEntity<>(userSettingsList, HttpStatus.OK);
//     }

//     // 세션 ID로 설정 조회
//     @GetMapping("/{sessionId}")
//     public ResponseEntity<UserSettings> getUserSettingsBySessionId(@PathVariable String sessionId) {
//         Optional<UserSettings> userSettings = userSettingsService.getUserSettingsBySessionId(sessionId);
//         // Optional이 비어있지 않을 때 (데이터가 있을 때)
//         return userSettings.map(settings -> new ResponseEntity<>(settings, HttpStatus.OK))
//                         .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//     }
// }