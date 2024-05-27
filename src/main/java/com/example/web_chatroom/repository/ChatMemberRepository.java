package com.example.web_chatroom.repository;

import com.example.web_chatroom.entity.ChatMember;
import com.example.web_chatroom.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMemberRepository extends MongoRepository<ChatMember,String> {
    List<ChatMember> findAllByUserId(String userId);

    boolean existsByUserIdAndChatRoom_ChatRoomId(String userId, String chatRoomId);
}
