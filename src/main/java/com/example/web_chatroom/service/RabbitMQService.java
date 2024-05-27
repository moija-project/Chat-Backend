package com.example.web_chatroom.service;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {
    @Autowired
    private final RabbitAdmin rabbitAdmin;

    @Autowired
    public RabbitMQService(RabbitAdmin rabbitAdmin, RabbitTemplate rabbitTemplate) {
        this.rabbitAdmin = rabbitAdmin;
    }

    public int getQueueCount(String exchangeName) {
        return rabbitAdmin.getQueueProperties(exchangeName).size();
    }
}