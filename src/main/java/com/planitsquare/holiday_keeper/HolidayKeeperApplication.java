package com.planitsquare.holiday_keeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HolidayKeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayKeeperApplication.class, args);
    }
}
