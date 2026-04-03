package com.interview.app.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Question {
    private String id;
    private String title;
    private String questionContent; // problem statement + examples (algorithm questions only)
    private String answer;          // reference answer / solution
    private String category;
    private String sourceFile;  // relative path from content root
    private int orderIndex;     // position within the source file
}
