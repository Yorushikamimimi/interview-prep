package com.interview.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "answer_records")
@Data
@NoArgsConstructor
public class AnswerRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "question_title")
    private String questionTitle;

    @Column(name = "category")
    private String category;

    // MASTERED | UNFAMILIAR | TO_REVIEW
    @Column(name = "mark", nullable = false)
    private String mark;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AnswerRecord(String questionId, String questionTitle, String category, String mark) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.category = category;
        this.mark = mark;
        this.createdAt = LocalDateTime.now();
    }
}
