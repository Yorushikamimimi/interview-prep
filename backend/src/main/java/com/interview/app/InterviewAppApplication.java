package com.interview.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class InterviewAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewAppApplication.class, args);
    }
}
