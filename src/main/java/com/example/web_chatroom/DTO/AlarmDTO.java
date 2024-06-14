package com.example.web_chatroom.DTO;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

public record AlarmDTO(String message, int pushType, LocalDateTime pubDate) {};
