package com.interview.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "interview")
@Data
public class InterviewProperties {
    private String contentPath;
}
