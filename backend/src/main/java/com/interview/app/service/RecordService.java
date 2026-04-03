package com.interview.app.service;

import com.interview.app.entity.AnswerRecord;
import com.interview.app.model.Question;
import com.interview.app.repository.AnswerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final AnswerRecordRepository repo;
    private final QuestionService questionService;

    // ── Submit a mark ──
    public AnswerRecord submit(String questionId, String mark) {
        validateMark(mark);
        Question q = questionService.getById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        AnswerRecord record = new AnswerRecord(questionId, q.getTitle(), q.getCategory(), mark);
        return repo.save(record);
    }

    // ── Latest mark for a single question ──
    public Optional<String> getLatestMark(String questionId) {
        return repo.findTopByQuestionIdOrderByCreatedAtDesc(questionId)
                .map(AnswerRecord::getMark);
    }

    // ── Full history for a single question ──
    public List<AnswerRecord> getHistory(String questionId) {
        return repo.findByQuestionIdOrderByCreatedAtDesc(questionId);
    }

    // ── Recent 100 records (history page) ──
    public List<AnswerRecord> getRecentRecords() {
        return repo.findTop100ByOrderByCreatedAtDesc();
    }

    // ── IDs of questions whose latest mark is TO_REVIEW ──
    public List<String> getToReviewIds() {
        return repo.findLatestToReview().stream()
                .map(AnswerRecord::getQuestionId)
                .toList();
    }

    // ── Stats: per-category mastery breakdown ──
    // Returns: [{ category, mastered, unfamiliar, toReview, total }]
    public List<Map<String, Object>> getCategoryStats() {
        List<Object[]> rows = repo.countByLatestMarkAndCategory();

        // category → { mark → count }
        Map<String, Map<String, Long>> byCategory = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String category = (String) row[0];
            String mark     = (String) row[1];
            Long   count    = (Long) row[2];
            byCategory.computeIfAbsent(category, k -> new HashMap<>())
                       .put(mark, count);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : byCategory.entrySet()) {
            Map<String, Long> marks = entry.getValue();
            long mastered    = marks.getOrDefault("MASTERED", 0L);
            long unfamiliar  = marks.getOrDefault("UNFAMILIAR", 0L);
            long toReview    = marks.getOrDefault("TO_REVIEW", 0L);
            long total       = mastered + unfamiliar + toReview;
            result.add(Map.of(
                "category",   entry.getKey(),
                "mastered",   mastered,
                "unfamiliar", unfamiliar,
                "toReview",   toReview,
                "total",      total
            ));
        }
        return result;
    }

    private void validateMark(String mark) {
        Set<String> valid = Set.of("MASTERED", "UNFAMILIAR", "TO_REVIEW");
        if (!valid.contains(mark)) {
            throw new IllegalArgumentException("Invalid mark: " + mark + ". Must be one of " + valid);
        }
    }
}
