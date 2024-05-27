package com.example.web_chatroom.service;

import com.example.web_chatroom.entity.ChatMember;
import com.example.web_chatroom.repository.ChatMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service@Slf4j@RequiredArgsConstructor
public class UserService {
    @Autowired
    private ChatMemberRepository chatMemberRepository;

    public boolean isRoomOwner(String userId, String chatRoomId) {
        return chatMemberRepository.existsByUserIdAndChatRoom_ChatRoomId(userId,chatRoomId);
    }
}
