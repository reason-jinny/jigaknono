package com.kt.jigaknono.controller;

import com.kt.jigaknono.domain.TransportSchedule;
import com.kt.jigaknono.service.TransportScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/schedules")
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" })
public class TransportScheduleController {

    @Autowired
    private TransportScheduleService transportScheduleService;

    // 전체 스케줄 조회
    @GetMapping
    public ResponseEntity<List<TransportSchedule>> getAllSchedules() {
        return ResponseEntity.ok(transportScheduleService.getAllSchedules());
    }

    // 스케줄 등록
    @PostMapping
    public ResponseEntity<TransportSchedule> createSchedule(@RequestBody TransportSchedule schedule) {
        return ResponseEntity.ok(transportScheduleService.saveSchedule(schedule));
    }

    // 스케줄 수정
    @PutMapping("/{id}")
    public ResponseEntity<TransportSchedule> updateSchedule(
            @PathVariable Long id,
            @RequestBody TransportSchedule schedule) {
        // ID 검증
        if (!id.equals(schedule.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            TransportSchedule updatedSchedule = transportScheduleService.saveSchedule(schedule);
            return ResponseEntity.ok(updatedSchedule);
        } catch (Exception e) {
            e.printStackTrace(); // 로깅
            return ResponseEntity.badRequest().build();
        }
    }

    // 스케줄 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        transportScheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }
}