package com.example.web_chatroom;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WebChatroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebChatroomApplication.class, args);

    }

}
