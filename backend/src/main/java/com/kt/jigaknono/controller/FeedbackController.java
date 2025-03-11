package com.kt.jigaknono.controller;

import com.kt.jigaknono.domain.Feedback;
import com.kt.jigaknono.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" })
public class FeedbackController {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback) {
        try {
            logger.info("Received feedback submission: {}", feedback);
            feedback.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
            Feedback savedFeedback = feedbackService.saveFeedback(feedback);
            return ResponseEntity.ok(savedFeedback);
        } catch (Exception e) {
            logger.error("Error submitting feedback", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFeedback() {
        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedback();
            logger.info("Retrieved {} feedbacks", feedbacks.size());
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            logger.error("Error retrieving feedbacks", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            logger.info("Resolving feedback {}: {}", id, request);
            Feedback resolvedFeedback = feedbackService.resolveFeedback(id, request.get("adminComment"));
            return ResponseEntity.ok(resolvedFeedback);
        } catch (Exception e) {
            logger.error("Error resolving feedback {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
