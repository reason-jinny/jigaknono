package com.kt.jigaknono.service;

import com.kt.jigaknono.domain.TransportSchedule;
import com.kt.jigaknono.repository.TransportScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportScheduleService {

    @Autowired
    private TransportScheduleRepository transportScheduleRepository;

    // 전체 스케줄 조회
    public List<TransportSchedule> getAllSchedules() {
        return transportScheduleRepository.findAll();
    }

    // 스케줄 저장 (등록/수정)
    public TransportSchedule saveSchedule(TransportSchedule schedule) {
        try {
            // null 체크 및 기본값 설정
            if (schedule.getTrafficDelay() == null) schedule.setTrafficDelay(0);
            if (schedule.getWalkDuration() == null) schedule.setWalkDuration(0);
            if (schedule.getWalkDistance() == null) schedule.setWalkDistance(0);
            
            return transportScheduleRepository.save(schedule);
        } catch (Exception e) {
            e.printStackTrace(); // 로깅
            throw e;
        }
    }

    // 스케줄 삭제
    public void deleteSchedule(Long id) {
        transportScheduleRepository.deleteById(id);
    }
} 