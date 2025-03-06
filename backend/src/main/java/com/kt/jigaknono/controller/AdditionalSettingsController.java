package com.kt.jigaknono.controller;

import com.kt.jigaknono.domain.AdditionalSettings;
import com.kt.jigaknono.service.AdditionalSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/additional-settings")
public class AdditionalSettingsController {

    @Autowired
    private AdditionalSettingsService additionalSettingsService;

    // 추가 설정 저장
    @PostMapping
    public ResponseEntity<AdditionalSettings> saveAdditionalSettings(
            @RequestBody AdditionalSettings additionalSettings) {
        AdditionalSettings savedSettings = additionalSettingsService.saveAdditionalSettings(additionalSettings);
        return new ResponseEntity<>(savedSettings, HttpStatus.CREATED);
    }

    // 모든 추가 설정 조회
    @GetMapping
    public ResponseEntity<List<AdditionalSettings>> getAllAdditionalSettings() {
        List<AdditionalSettings> additionalSettings = additionalSettingsService.getAllAdditionalSettings();
        return new ResponseEntity<>(additionalSettings, HttpStatus.OK);
    }
}