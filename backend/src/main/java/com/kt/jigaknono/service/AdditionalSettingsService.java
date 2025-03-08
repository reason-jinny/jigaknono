// package com.kt.jigaknono.service;

// import com.kt.jigaknono.domain.AdditionalSettings;
// import com.kt.jigaknono.repository.AdditionalSettingsRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;

// @Service
// public class AdditionalSettingsService {

//     @Autowired
//     private AdditionalSettingsRepository additionalSettingsRepository;

//     public AdditionalSettings saveAdditionalSettings(AdditionalSettings additionalSettings) {
//         return additionalSettingsRepository.save(additionalSettings);
//     }

//     public List<AdditionalSettings> getAllAdditionalSettings() {
//         return additionalSettingsRepository.findAll();
//     }
// }