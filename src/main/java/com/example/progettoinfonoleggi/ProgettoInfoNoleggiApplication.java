package com.example.progettoinfonoleggi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProgettoInfoNoleggiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgettoInfoNoleggiApplication.class, args);
    }
}
