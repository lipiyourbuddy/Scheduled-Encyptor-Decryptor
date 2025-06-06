package com.example.scheduled_encryptor.config;

import org.quartz.Scheduler;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerStarter {

	@Bean
	public ApplicationRunner quartzSchedulerStarter(Scheduler scheduler) {


        return args -> {
            if (!scheduler.isStarted()) {
                scheduler.start(); 
                System.out.println("Quartz Scheduler started!");
            }
        };
    }
}
