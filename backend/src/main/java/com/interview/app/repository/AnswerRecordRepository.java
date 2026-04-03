package com.interview.app.repository;

import com.interview.app.entity.AnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnswerRecordRepository extends JpaRepository<AnswerRecord, Long> {

    // Latest mark for a question (for "current status" display)
    Optional<AnswerRecord> findTopByQuestionIdOrderByCreatedAtDesc(String questionId);

    // All records for a question (history view)
    List<AnswerRecord> findByQuestionIdOrderByCreatedAtDesc(String questionId);

    // Recent records across all questions (history page)
    List<AnswerRecord> findTop100ByOrderByCreatedAtDesc();

    // All question IDs marked TO_REVIEW (latest mark per question)
    @Query("""
        SELECT r FROM AnswerRecord r
        WHERE r.createdAt = (
            SELECT MAX(r2.createdAt) FROM AnswerRecord r2
            WHERE r2.questionId = r.questionId
        )
        AND r.mark = 'TO_REVIEW'
        ORDER BY r.createdAt DESC
    """)
    List<AnswerRecord> findLatestToReview();

    // Category stats: count of each mark per category (latest mark per question)
    @Query("""
        SELECT r.category, r.mark, COUNT(r) FROM AnswerRecord r
        WHERE r.createdAt = (
            SELECT MAX(r2.createdAt) FROM AnswerRecord r2
            WHERE r2.questionId = r.questionId
        )
        GROUP BY r.category, r.mark
    """)
    List<Object[]> countByLatestMarkAndCategory();
}
