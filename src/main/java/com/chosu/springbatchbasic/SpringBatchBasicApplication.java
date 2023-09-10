package com.chosu.springbatchbasic;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchBasicApplication.class, args);
    }

}
