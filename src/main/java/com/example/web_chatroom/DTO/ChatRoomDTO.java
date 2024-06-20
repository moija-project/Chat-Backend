package com.example.web_chatroom.DTO;

import com.example.web_chatroom.entity.ChatRoom;

import java.time.ZonedDateTime;

public record ChatRoomDTO(String lastChat, ZonedDateTime receivedTime, int nonRead, ChatRoom chatRoom) {
}
