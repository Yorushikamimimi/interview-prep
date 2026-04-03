package com.interview.app.controller;

import com.interview.app.model.Question;
import com.interview.app.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // GET /api/categories
    // Returns: [{ "name": "Redis", "count": 20 }, ...]
    @GetMapping("/categories")
    public List<Map<String, Object>> getCategories() {
        return questionService.getCategoryStats();
    }

    // GET /api/questions?categories=Redis,JVM&mode=sequential
    // mode: sequential (default) | random
    @GetMapping("/questions")
    public List<Question> getQuestions(
            @RequestParam(required = false) List<String> categories,
            @RequestParam(defaultValue = "sequential") String mode) {
        boolean shuffle = "random".equalsIgnoreCase(mode);
        return questionService.getQuestions(categories, shuffle);
    }

    // GET /api/questions/{id}
    @GetMapping("/questions/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable String id) {
        return questionService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
