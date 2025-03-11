package com.kt.jigaknono.service;

import com.kt.jigaknono.domain.Feedback;
import com.kt.jigaknono.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public Feedback resolveFeedback(Long id, String adminComment) {
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("피드백을 찾을 수 없습니다."));
        
        feedback.setIsResolved(true);
        feedback.setAdminComment(adminComment);
        
        return feedbackRepository.save(feedback);
    }
}
