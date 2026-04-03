package com.interview.app.service;

import com.interview.app.model.Question;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Component
public class MarkdownParser {

    // ── 八股文 / 项目面经：按 ## 标题拆分，每个标题 = 一道题 ──
    public List<Question> parseSplitFile(Path file, String category, Path contentRoot) throws IOException {
        String raw = Files.readString(file, StandardCharsets.UTF_8);
        String relativePath = contentRoot.relativize(file).toString().replace('\\', '/');

        List<Question> questions = new ArrayList<>();
        // Split on lines that start with "## " (level-2 headings)
        String[] chunks = raw.split("(?m)^## ");

        int index = 0;
        for (String chunk : chunks) {
            if (chunk.isBlank()) continue;

            int newline = chunk.indexOf('\n');
            if (newline < 0) continue;

            String rawTitle = chunk.substring(0, newline).trim();
            String answer = chunk.substring(newline + 1).trim();

            // Skip H1 headers and preamble chunks (content before first ## heading)
            if (rawTitle.startsWith("#")) continue;
            if (rawTitle.isBlank() || answer.isBlank()) continue;

            // Strip leading "1. " / "12. " number prefixes
            String title = rawTitle.replaceFirst("^\\d+\\.\\s+", "");

            questions.add(Question.builder()
                    .id(generateId(relativePath, index))
                    .title(title)
                    .answer(answer)
                    .category(category)
                    .sourceFile(relativePath)
                    .orderIndex(index)
                    .build());
            index++;
        }
        return questions;
    }

    // ── 算法题：整个文件 = 一道题 ──
    // For Leetcode-style files (starts with ### heading), split problem statement from solution.
    public Question parseAlgorithmFile(Path file, String category, Path contentRoot) throws IOException {
        String content = Files.readString(file, StandardCharsets.UTF_8).trim();
        if (content.isBlank()) return null;

        String relativePath = contentRoot.relativize(file).toString().replace('\\', '/');
        String filename = file.getFileName().toString();
        String title = filename.endsWith(".md")
                ? filename.substring(0, filename.length() - 3)
                : filename;

        // Try to split questionContent (problem + examples) from answer (solution)
        // Only for files that start with a ### heading (Leetcode-style)
        String questionContent = null;
        String answer = content;

        if (content.startsWith("### ") || content.startsWith("## ")) {
            java.util.regex.Pattern solutionMarker = java.util.regex.Pattern.compile(
                    "(?m)^#{2,3} (核心思路|解题思路|解法|ACM|LeetCode 提交)"
            );
            java.util.regex.Matcher m = solutionMarker.matcher(content);
            if (m.find()) {
                questionContent = content.substring(0, m.start()).trim();
                answer = content.substring(m.start()).trim();
            }
        }

        return Question.builder()
                .id(generateId(relativePath, 0))
                .title(title)
                .questionContent(questionContent)
                .answer(answer)
                .category(category)
                .sourceFile(relativePath)
                .orderIndex(0)
                .build();
    }

    // ── Derive category name from 八股文 filename ──
    // e.g. "Java基础 八股文.md" → "Java基础"
    //      "Spring ， Spring Boot 八股文.md" → "Spring"
    public String categoryFromBaguFilename(String filename) {
        String name = filename.endsWith(".md") ? filename.substring(0, filename.length() - 3) : filename;
        // Strip trailing "八股文" and surrounding whitespace / punctuation
        name = name.replaceAll("[\\s，,]+八股文.*$", "").trim();
        // Special-case abbreviations
        return switch (name) {
            case "计算机网络" -> "计网";
            default -> name;
        };
    }

    // ── Stable deterministic ID: SHA-256(path + "#" + index), first 12 hex chars ──
    private String generateId(String relativePath, int index) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((relativePath + "#" + index).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(String.format("%02x", hash[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return relativePath.hashCode() + "_" + index;
        }
    }
}
