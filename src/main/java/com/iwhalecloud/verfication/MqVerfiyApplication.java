package com.iwhalecloud.verfication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MqVerfiyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqVerfiyApplication.class, args);
    }

}
