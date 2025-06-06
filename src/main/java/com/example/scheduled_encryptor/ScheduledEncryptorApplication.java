package com.example.scheduled_encryptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ScheduledEncryptorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduledEncryptorApplication.class, args);
    }
}

