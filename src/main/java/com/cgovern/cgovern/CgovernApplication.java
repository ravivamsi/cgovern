package com.cgovern.cgovern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CgovernApplication {

    public static void main(String[] args) {
        SpringApplication.run(CgovernApplication.class, args);
    }
}
