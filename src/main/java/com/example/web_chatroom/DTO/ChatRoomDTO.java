package com.example.web_chatroom.DTO;

import com.example.web_chatroom.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomDTO(String lastChat, LocalDateTime receivedTime, int nonRead, ChatRoom chatRoom) {
}
