package com.interview.app.controller;

import com.interview.app.entity.AnswerRecord;
import com.interview.app.model.Question;
import com.interview.app.service.QuestionService;
import com.interview.app.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final QuestionService questionService;

    // POST /api/records
    // Body: { "questionId": "abc123", "mark": "MASTERED" }
    @PostMapping("/records")
    public ResponseEntity<?> submitMark(@RequestBody Map<String, String> body) {
        try {
            String questionId = body.get("questionId");
            String mark = body.get("mark");
            if (questionId == null || mark == null) {
                return ResponseEntity.badRequest().body("questionId and mark are required");
            }
            AnswerRecord saved = recordService.submit(questionId, mark);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/records/history
    // Recent 100 records for the history page
    @GetMapping("/records/history")
    public List<AnswerRecord> getHistory() {
        return recordService.getRecentRecords();
    }

    // GET /api/records/history/{questionId}
    // All records for a specific question
    @GetMapping("/records/history/{questionId}")
    public List<AnswerRecord> getQuestionHistory(@PathVariable String questionId) {
        return recordService.getHistory(questionId);
    }

    // GET /api/records/mark/{questionId}
    // Latest mark for a question
    @GetMapping("/records/mark/{questionId}")
    public ResponseEntity<Map<String, String>> getLatestMark(@PathVariable String questionId) {
        return recordService.getLatestMark(questionId)
                .map(m -> ResponseEntity.ok(Map.of("mark", m)))
                .orElse(ResponseEntity.ok(Map.of("mark", "")));
    }

    // GET /api/records/to-review
    // Questions whose latest mark is TO_REVIEW
    @GetMapping("/records/to-review")
    public List<Question> getToReview() {
        return recordService.getToReviewIds().stream()
                .flatMap(id -> questionService.getById(id).stream())
                .toList();
    }

    // GET /api/stats
    // Per-category mastery breakdown
    @GetMapping("/stats")
    public List<Map<String, Object>> getStats() {
        return recordService.getCategoryStats();
    }
}
