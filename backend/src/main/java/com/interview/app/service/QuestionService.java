package com.interview.app.service;

import com.interview.app.config.InterviewProperties;
import com.interview.app.model.Question;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final InterviewProperties props;
    private final MarkdownParser parser;

    // In-memory question store, loaded once at startup
    private final List<Question> allQuestions = new ArrayList<>();
    private final Map<String, Question> questionById = new HashMap<>();

    @PostConstruct
    public void load() {
        Path root = Path.of(props.getContentPath()).normalize().toAbsolutePath();
        log.info("Loading questions from: {}", root);

        if (!Files.exists(root)) {
            log.warn("Content path does not exist: {}", root);
            return;
        }

        try {
            loadBaguFiles(root);
            loadAlgorithmFiles(root);
            loadProjectFiles(root);
        } catch (Exception e) {
            log.error("Failed to load questions", e);
        }

        // Build id → question index
        allQuestions.forEach(q -> questionById.put(q.getId(), q));
        log.info("Loaded {} questions across {} categories",
                allQuestions.size(), getCategoryNames().size());
    }

    // ── 八股文：面试/面经/后端/八股/*.md ──
    private void loadBaguFiles(Path root) throws IOException {
        Path baguDir = root.resolve("面经/后端/八股");
        if (!Files.exists(baguDir)) return;

        try (Stream<Path> files = Files.list(baguDir)) {
            files.filter(f -> f.toString().endsWith(".md"))
                 .sorted()
                 .forEach(file -> {
                     String category = parser.categoryFromBaguFilename(
                             file.getFileName().toString());
                     try {
                         List<Question> questions = parser.parseSplitFile(file, category, root);
                         allQuestions.addAll(questions);
                         log.debug("Parsed {} questions from {}", questions.size(), file.getFileName());
                     } catch (IOException e) {
                         log.warn("Failed to parse {}: {}", file, e.getMessage());
                     }
                 });
        }
    }

    // ── 算法题：面试/算法题/**/*.md ──
    private void loadAlgorithmFiles(Path root) throws IOException {
        Path algoDir = root.resolve("算法题");
        if (!Files.exists(algoDir)) return;

        // Skip meta files (prompt files, record files)
        Set<String> skipFiles = Set.of("算法题prompt.md", "算法题记录.md", "算法题常背基础函数模板.md");

        try (Stream<Path> files = Files.walk(algoDir)) {
            files.filter(f -> f.toString().endsWith(".md"))
                 .filter(f -> !skipFiles.contains(f.getFileName().toString()))
                 .sorted()
                 .forEach(file -> {
                     try {
                         Question q = parser.parseAlgorithmFile(file, "算法题", root);
                         if (q != null) allQuestions.add(q);
                     } catch (IOException e) {
                         log.warn("Failed to parse {}: {}", file, e.getMessage());
                     }
                 });
        }
    }

    // ── 项目面经：面试/面经/项目面经/*.md ──
    private void loadProjectFiles(Path root) throws IOException {
        Path projDir = root.resolve("面经/项目面经");
        if (!Files.exists(projDir)) return;

        try (Stream<Path> files = Files.list(projDir)) {
            files.filter(f -> f.toString().endsWith(".md"))
                 .sorted()
                 .forEach(file -> {
                     try {
                         List<Question> questions = parser.parseSplitFile(file, "项目面经", root);
                         allQuestions.addAll(questions);
                     } catch (IOException e) {
                         log.warn("Failed to parse {}: {}", file, e.getMessage());
                     }
                 });
        }
    }

    // ── Public API ──

    public List<Question> getQuestions(List<String> categories, boolean shuffle) {
        List<Question> result = (categories == null || categories.isEmpty())
                ? new ArrayList<>(allQuestions)
                : allQuestions.stream()
                        .filter(q -> categories.contains(q.getCategory()))
                        .toList();

        if (shuffle) {
            result = new ArrayList<>(result);
            Collections.shuffle(result);
        }
        return result;
    }

    public Optional<Question> getById(String id) {
        return Optional.ofNullable(questionById.get(id));
    }

    public List<Map<String, Object>> getCategoryStats() {
        Map<String, Long> counts = new LinkedHashMap<>();
        allQuestions.forEach(q ->
                counts.merge(q.getCategory(), 1L, Long::sum));

        return counts.entrySet().stream()
                .map(e -> Map.<String, Object>of("name", e.getKey(), "count", e.getValue()))
                .toList();
    }

    private Set<String> getCategoryNames() {
        Set<String> names = new LinkedHashSet<>();
        allQuestions.forEach(q -> names.add(q.getCategory()));
        return names;
    }
}
